---
sidebar_position: 1
---

# CVZ Examples

This site is meant to be a community-lead example driven [ZIO](https://zio.dev) documentation site. It built
on [docusaurus](https://docusaurus.io/) and uses [mdoc](https://scalameta.org/mdoc/) to generate typechecked markdown
documentation for Scala.

The documentation is structured as zio-projects (e.g. `zio` and `zio-prelude`) being the top-level menu item, and
various examples of using that project/library therein.

## Library Versions

Here is a summary table of the dependencies and versions used to compile the examples.

| Org | Package | Version|
|---|---|---|
| dev.zio |zio | @ZIO_VERSION@ |
| dev.zio |zio-test | @ZIO_TEST_VERSION@ |
| dev.zio |zio-prelude | @PRELUDE_VERSION@ |
| dev.zio |zio-json | @ZIO_JSON_VERSION@ |
| dev.zio |zio-optics | @ZIO_OPTICS_VERSION@ |
| io.getquill | quill-jdbc-zio | @ZIO_QUILL_VERSION@ |

## Contributing

If you would like to contribute an example, just code it up and submit a PR! You can view some current examples for
insight for inspiration.

### Tooling

The site is built with docusaurus, so if you're first starting up the project, you can
`yarn install` to grab the site dependencies, and `yarn start` which will kick off a "live reload" service that will
update changes in realtime. Note that it looks for docs to process in a folder named `/docs` - and that's where `/mdocs`
comes in!

There is a `build.sbt` file set up to also load this as an SBT project, to process files in the `mdocs` folder, and
output them to the `docs` folder. In a separate terminal, run `sbt "mdoc --watch"`, and that will start a live-watch
process to constantly compile and update and docs that have changed from `/mdocs` to `/docs`. A script has been added to
the `package.json` file that alias this as `yarn sbt-watch`, if you choose to use it.

So to summarize the steps:

0. yarn install
1. Terminal 1 > yarn sbt-watch
2. Terminal 2 > yarn start
3. Write some docs, and preview live!

### Docs

Only the `modcs` folder is set up to be processed by mdoc. To write a Scala that will be compiled, simple
add `scala mdoc` to the code-fence. For example:

```scala mdoc
import scala.util.Random
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

println("This should be different on every re-compile!")
val anInt = Random.nextInt()

println("You can even use Futures here!")
implicit val ec = ExecutionContext.global
val lala = Future(throw new Exception("Oh No!"))
```

If you are using IntelliJ idea, you can "inject language" into the codefence, and get Scala code
highlighting/autocomplete as you would usually use it! Because you have to add the word `mdoc` to the code-fence, it
seems to break support for auto recognizing it as Scala, but works if you manually set it!

### Blog

If you add an example, you are more than welcome to add a blog entry to note/highlight the addition. The general file
naming format is `YYYY-MM-DD-title.md` without the following structure for the front matter with your GitHub user info:

```
---
title: ZIO!
author: Alterationx10
author_url: https://github.com/alterationx10
author_image_url: https://avatars1.githubusercontent.com/u/149476?s=460&v=4
tags: [general zio zio-prelude]
---
```

Don't feel like you have to add a blog post if you don't want to, and feel free to use your GitHub username for the
author field!
