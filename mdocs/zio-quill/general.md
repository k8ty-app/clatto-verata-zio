---
sidebar_position: 1
title: General Usage
---

[Quill](https://getquill.io) is a wonderful project that helps generate SQL (or other query languages) using a Quoted Domain Specific Language (QDSL).  It provides an 
optional library that integrates very nicely with ZIO.  When used together you don't need anything other than a JDBC driver to work with a SQL 
Database.

Here's how things work as a basic example.  This is likely not how you'd set it up for production use and is meant to simply demonstrate how all the pieces work.  See the (structured)[/docs/zio-quill/structured] example
for how you would work this into a DAO/Repository based on the [Service Module Pattern](/docs/zio/service_module_pattern).

In this example we have one entity we are working with, an Item:

## The Data Model

```scala
import java.time.Instant

case class Item(id: Long, name: String, description: String, unitPrice: Double, createdAt: Instant = Instant.now)
```
It represents a row in the following table (Postgresql):
```postgresql
create table item (
    id serial primary key,
    name varchar(255) not null,
    description text not null,
    unit_price decimal not null,
    created_at timestamp not null
);
```

##The Boilerplate 
Quill operates in the scope of a Context.  `zio-quill` provides a `ZioJdbcContext` and in our case we want the specialized `PostgresZioJdbcContext` since we are working
with a Postgres database.

```scala
  // new ZIO JDBC Context for Quill!
  val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._
```

You must import the context so that the dsl is brought into scope of your code.

The next boilerplate is handy - but defaults are generated if you don't provide them.  We want to do something
specific when we are inserting `Items` - and we want to be able to compose our quoted queries and override the table name we 
are mapping the Item to - so we provide an `InsertMeta` and a `SchemaMeta`:

```scala
  // some Meta classes to help Quill
  implicit val itemSchemaMeta = schemaMeta[Item]("item")
  implicit val itemInsertMeta = insertMeta[Item](_.id)
```

One of the items that must be within the scope of the `ZioJdbcContext` are any `Encoders` or `Decoders` you have for types that 
aren't handled by Quill natively.  In our case we have an `Instant` that we will need to convert to a `javax.sql.Timestamp`.


```scala
  // some Encoders for Instant so Quill knows what to do with an Instant
  implicit val instantEncoder: Encoder[Instant] = encoder(Types.TIMESTAMP, (index, value, row) => row.setTimestamp(index, Timestamp.from(value)))
  implicit val instantDecoder: Decoder[Instant] = decoder((index, row) => { row.getTimestamp(index).toInstant })
```
If you have a type that can be an extension of an existing type Quill does know about you can use a `MappedEncoding` to map your type
to the supporting type and back again.  Sorry, but that is not demonstrated here. :)

## Queries
There's a lot of great documentation on [Quill's website](https://getquill.io/#quotation-queries) that goes in depth into how to build queries.  In our case we have two VERY simple ones:
an `insert` and a `select`. 

```scala
  // some Quill queries
  val itemsQuery             = quote(query[Item])
  def insertItem(item: Item) = quote(itemsQuery.insert(lift(anItem)))
```

To filter a query, you can use `filter` against the case class that maps to a of the table you're working with.  See Quill's documenation
for further details.

## ZLayer
In order to work with the `ZioJdbcContext` we need to have access to a `QDataSource` and a `QConnection`.  We get these via a `ZLayer`
and some helpers.

```scala
  // simple layer providing a connection for the effect; this is pulled from a HikariCP
  // NOTE - prefix is the HOCON prefix in the application.conf to look for
  val zioConn: ZLayer[Blocking, Throwable, QConnection] =
  QDataSource.fromPrefix("zioQuillExample") >>> QDataSource.toConnection
```
There are a number of `from*` methods to help you load a config - which leverages the Typesafe Config HOCON format via an `application.conf`.  It expects something along these lines:

```properties
zioQuillExample {
    connectionTimeout=30000
    dataSourceClassName=org.postgresql.ds.PGSimpleDataSource
    dataSource {
        user=zio_user
        password=magical_password
        databaseName=zio_playground
        portNumber=5432
        serverName=localhost
    }
}
```
You can also use a JDBC url if you provide the `url` property to the `dataSource` section above.

## Executing Queries
Executing queries is pretty straight forward.  You will win up using a for-comprehension to compose various `ZIO`results from running them.  You can even wrap them 
in a transaction!

```scala
  // the transactional use of the context (this belongs in a DAO/Repository ZIO Service module)
  val insertAndQuery: RIO[QConnection, List[Item]] = ctx.transaction {
    for {
      _     <- ctx.run(insertItem(anItem))
      items <- ctx.run(itemsQuery)
    } yield items
  }
```

Yup - its that simple!

Now let's create something to run through it and execute a ZIO program:

```scala
  // an item to insert...
  val anItem: Item = Item(id = -1, name = "Boomstick", description = "This...is my Boomstick!", unitPrice = 255.50, Instant.now)
```
This...is our boomstick!  Wait - the id is -1?  Recall the `InsertMeta` above in the boilerplate section?  That tells 
Quill what fields of your case class to ignore when performing an update.  Since our table has a `serial` datatype (which generates
an auto-incremented sequence in Postgres) we don't need to provide the `id` field upon insert.  So we set the value to some bogus value
knowing that Quill will drop it and the database will generate a new sequential value for us.

Here is our little `ZIO` program:

```scala
// our program!
  val program: RIO[Console with QConnection, Unit] = for {
    _     <- putStrLn("Running zio-quill example...")
    items <- insertAndQuery
    _     <- putStrLn(s"Items ==> $items")
  } yield ()
```

And let's provide it the `QConnection` from the `ZLayer` we built above and run it...

```scala
program.provideLayer(ZEnv.live ++ zioConn).run
```

And that's it!  

The whole example - altogether now:
```scala
import io.getquill.context.ZioJdbc.{QConnection, QDataSource}
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio._
import zio.blocking.Blocking
import zio.console._

import java.sql.{Timestamp, Types}
import java.time.Instant

object ZioQuillExample extends App {
  // new ZIO JDBC Context for Quill!
  val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  // some Meta classes to help Quill
  implicit val itemSchemaMeta = schemaMeta[Item]("item")
  implicit val itemInsertMeta = insertMeta[Item](_.id)

  // some Encoders for Instant so Quill knows what to do with an Instant
  implicit val instantEncoder: Encoder[Instant] = encoder(Types.TIMESTAMP, (index, value, row) => row.setTimestamp(index, Timestamp.from(value)))
  implicit val instantDecoder: Decoder[Instant] = decoder((index, row) => { row.getTimestamp(index).toInstant })

  // simple layer providing a connection for the effect; this is pulled from a HikariCP
  // NOTE - prefix is the HOCON prefix in the application.conf to look for
  val zioConn: ZLayer[Blocking, Throwable, QConnection] =
  QDataSource.fromPrefix("zioQuillExample") >>> QDataSource.toConnection

  // an item to insert...
  val anItem: Item = Item(id = -1, name = "Boomstick", description = "This...is my Boomstick!", unitPrice = 255.50, Instant.now)

  // some Quill queries
  val itemsQuery             = quote(query[Item])
  def insertItem(item: Item) = quote(itemsQuery.insert(lift(anItem)))

  // the transactional use of the context (this belongs in a DAO/Repository ZIO Service module)
  val insertAndQuery: RIO[QConnection, List[Item]] = ctx.transaction {
    for {
      _     <- ctx.run(insertItem(anItem))
      items <- ctx.run(itemsQuery)
    } yield items
  }

  // our program!
  val program: RIO[Console with QConnection, Unit] = for {
    _     <- putStrLn("Running zio-quill example...")
    items <- insertAndQuery
    _     <- putStrLn(s"Items ==> $items")
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(ZEnv.live ++ zioConn).exitCode
}


```