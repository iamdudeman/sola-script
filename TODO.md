# TODO list

* consider implementing optional chaining "?." for dealing with null conveniently
    * callOptional
        * parser support
        * resolver support
        * interpreter support

## Cleanup

* add missing JavaDocs

## Features

* implement break keyword within loops
* implement arrays
* consider adding a way to return a result from SolaScript#execute

## Syntax sugar

* map creation with values predefined (syntactic sugar)
* for statements

## Handle common errors

* handle when a local variable is never used (report an error perhaps?)
* protect divide by zero in binary expression interpreter
    * maybe throw an error?
    * or should it be "Infinity"

## Standard library

* read file
* write file
