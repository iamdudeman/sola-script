# TODO list

* improve how standard library code is added (maybe a common interface that ScriptRuntime can import from?)
    * add more to standard library
    * make it easier to add custom library code
* if statements
* while statements
* function declarations
* return statements
* class declarations
* for statements
* support ternary operator (probably between assignment and logic_or)
    * needs new tokens "?" and ":"
* consider implementing optional chaining "?." for dealing with null conveniently
    * if so then also consider implementing nullish coalescence "??"
* protect divide by zero in binary expression interpreter
    * maybe throw an error?
    * or should it be "Infinity"
