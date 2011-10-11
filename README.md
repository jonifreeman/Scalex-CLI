A command line interface for Scalex
===================================

[Scalex](http://scalex.org) is a cool Hoogle style index for Scaladocs.

Install
-------

* Download files 'scalex' and 'scalex-cli.jar' from 'bin/'
* Put those files to some dir in your PATH
* Try it:

  scalex 'List[A] => A'
  scalex 'List[A] => (A => B) => List[B]'
  ...

Run with SBT
------------

* Clone this project
* Install SBT 0.11
* Start SBT console
* in SBT console: update
* Then...
* run List
* run List[A]=>A
* ...

TODO
----

* Improved error handling
