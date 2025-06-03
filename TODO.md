# TODO list

* tokenizer support
* grammar updates
* parser support
* resolver support
* interpreter support

## Features

* implement break keyword within loops
* implement arrays
* support ternary operator (probably between assignment and logic_or)
    * needs new tokens "?" and ":"
* consider implementing optional chaining "?." for dealing with null conveniently

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
