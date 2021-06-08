---
sidebar_position: 2
title: Structured Usage
---

The general overview hopefully helps you understand how `zio-quill` works in general.  But in 
real life, you probably are building services and repositories for your application.  So
how would you do that here?  Because it looks like QConnection would be re-used for your
entire DAO/Repository over its lifespan - which is clearly not what you want ina production
scenario.

NOTE - we choose to use the term 'Repository' vs DAO; they are basically the same concept.

Let's work through this...

## Getting Started
Let's re-stablish our data model.  In this example we'll continue to work with the `Item` model
as before:

```scala
  case class ItemRecord(id: Long = -1, name: String, description: String, unitPrice: Double, createdAt: Instant = Instant.now)
```

We are going to employ the _context trait_ pattern as suggested by Quill in its [documentation](https://getquill.io/#contexts-dependent-contexts-modular-contexts).
As such we define our own QuillContext we mix in some encoders so we can easily use them anywhere we use our QuillContext.
```scala
  class MyZioContext[N <: NamingStrategy](override val naming: N) extends PostgresZioJdbcContext[N](naming) with InstantEncoding

  trait InstantEncoding { this: JdbcRunContext[_, _] =>
    implicit val instantDecoder: Decoder[Instant] = decoder((index, row) => { row.getTimestamp(index).toInstant })
    implicit val instantEncoder: Encoder[Instant] = encoder(Types.TIMESTAMP, (idx, value, row) => row.setTimestamp(idx, Timestamp.from(value)))
  }
```
Next, let's define our repository.

## The ZIO Service (Repository)
We are going to use the Service Module v2.0 to build up our repository.

### Service and Query Traits
First up the service trait:

```scala
trait ItemRepository extends Queries {
  def create(item: ItemRecord): Task[ItemRecord]
  def all: Task[Seq[ItemRecord]]
  def findById(id: Long): Task[ItemRecord]
}
```

Next - let's start with the Quill queries - again using the context traits pattern:

```scala
trait Queries {
  val ctx: MyZioContext[_]

  import ctx._

  // NOTE - if you put the type here you get a 'dynamic query' - which will never wind up working...
  implicit val itemSchemaMeta = schemaMeta[ItemRecord]("item")
  implicit val itemInsertMeta = insertMeta[ItemRecord](_.id)

  val itemsQuery                   = quote(query[ItemRecord])
  def byId(id: Long)               = quote(itemsQuery.filter(_.id == lift(id)))
  def insertItem(item: ItemRecord) = quote(itemsQuery.insert(lift(item)))
}
```

### Live Implementation
Now we need to build the 'live' implementation for our respository.  But how do we go about this?  We can start by 
creating a case class that receives the `QDataSource`.  Using this we are guaranteed that Quill will give us a fresh connection
from the backing Hikari connection pool each time the effects that access the database execute.

A `QDataSource` is a type alias for `Has[DataSource with Closable] with Blocking` - so to inject these into your live implementation you need
to reference the services behind those.  What your live implementation will need is basically two items: the `DataSource with Closeable` and a reference
to the blocking service, `Blocking.Service`.

Here's what that will look like:

```scala
import io.getquill.SnakeCase
import io.getquill.context.ZioJdbc.QuillZioExt
import zio._
import zio.blocking.Blocking

import java.io.Closeable
import javax.sql.DataSource

case class ItemRepositoryLive(dataSource: DataSource with Closeable, blocking: Blocking.Service) extends ItemRepository with Queries {
  val dataSourceLayer: Has[DataSource with Closeable] with Has[Blocking.Service] = Has.allOf[DataSource with Closeable, Blocking.Service](dataSource, blocking)

  val ctx: MyZioContext[SnakeCase] = new MyZioContext[SnakeCase](SnakeCase)

  import ctx._

  override def create(item: ItemRecord): Task[ItemRecord] = ctx.transaction {
    for {
      _     <- ctx.run(insertItem(item))
      items <- ctx.run(itemsQuery)
    } yield items.headOption.getOrElse(throw new Exception("Insert failed!"))
  }.dependOnDataSource().provide(dataSourceLayer)

  override def all: Task[Seq[ItemRecord]] = ctx.run(itemsQuery).dependOnDataSource().provide(dataSourceLayer)

  override def findById(id: Long): Task[ItemRecord] =
    ctx.run(byId(id)).map(_.headOption.getOrElse(throw new Exception(s"Can't find for id $id"))).dependOnDataSource().provide(dataSourceLayer)

}
```

Let's unpack this a bit...

First off - note that we create a `dataSourceLayer` by wrapping the constructor parameter values with `Has` again.  The reason why will become clear soon...

Next, we create a new instance of our `MyZioContext` that we created using the `SnakeCase` `NamingStrategy`.  This can be however your
database and thus context needs it.

Be sure to then import the context - `import ctx._` so that it's available for the rest of your implementation.

Now we can implement our repository method using quill as we normally work.  Invoke the queries via `ctx.run` and manipulate the results as necessary.

But notice the final part of the impelmentation methods - the call to `dependsOnDataSource().provide(dataSourceLayer)`.  What this does is enables your effect
to utilize the passed in datasource.  Which will in turn grab a new connection and make it available for your queries.

### Service Companion Object
Now let's wrap all this up by creating the layer and providing accessor methods:

```scala
object ItemRepository {

  def create(item: ItemRecord): RIO[Has[ItemRepository], ItemRecord] = ZIO.serviceWith[ItemRepository](_.create(item))
  def all: RIO[Has[ItemRepository], Seq[ItemRecord]]                 = ZIO.serviceWith[ItemRepository](_.all)
  def findById(id: Long): RIO[Has[ItemRepository], ItemRecord]       = ZIO.serviceWith[ItemRepository](_.findById(id))

  // live layer
  val layer: URLayer[QDataSource, Has[ItemRepository]] = (ItemRepositoryLive(_, _)).toLayer

}
```

