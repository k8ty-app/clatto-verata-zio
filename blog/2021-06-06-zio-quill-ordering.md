---
title: More ZIO Quill, ZIO Prelude Ordering & ZIO Optics
author: Terry Drozdowski
author_url: https://github.com/tdrozdowski
author_image_url: https://avatars.githubusercontent.com/u/1784034?s=400&v=4
tags: [zio, zio-quill]
---

After a bit of a delay, the second part of the `zio-quill` example is complete - the [structured](/docs/zio-quill/structured) exampled.  This
is a potential pattern that you can use to build up DAOs/Repositories using the ZIO Service Module 2.0 pattern - and leverage the HikariCP behind 
Quill's QDataSource.  

Also a conversation came up in the `zio-prelude` discord channel regarding `Ord` and `Ordering`.  An example was provided in Scala 3 - I've taken
the example and back ported it to Scala 2 using `Enumeration` (vs the Scala 3 `enum`) and provide it [here](/docs/zio-prelude/ordering).  Its a neat
little 'Rock, Paper, Scissors' example.

Finally, `zio-optics` was recently introduced by Adam Fraser and Kit in a recent Zymposium.  Its currently version `0.1.0` and very early in.  However, the 
basics are there and they do work!  Check out the documentation they have (which is based on a snapshot version past the current 'stable' version - so beware) [here](https://zio.github.io/zio-optics/docs/overview/overview_index).
You can find our example and basic usage [here](/docs/zio-optics/general).