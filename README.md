# sola-script

sola-script is an interpreted programming language that I built to learn more about how programming languages are
designed and implemented. It was developed while working through the material in the
book [Crafting Interpreters](https://craftinginterpreters.com/) by Robert Nystrom.

[![Java CI](https://github.com/iamdudeman/sola-script/actions/workflows/ci_build.yml/badge.svg)](https://github.com/iamdudeman/sola-script/actions/workflows/ci_build.yml)
[![Javadocs Link](https://img.shields.io/badge/Javadocs-blue.svg)](https://iamdudeman.github.io/sola-script/)
[![](https://jitpack.io/v/iamdudeman/sola-script.svg)](https://jitpack.io/#iamdudeman/sola-script)


## Sola grammar

```
program              := declaration* EOF ;
```

### Declarations

```
declaration          := funDecl | varDecl | valDecl | statement ;
funDecl              := "fun" function ;
varDecl              := "var" IDENTIFIER ( "=" expression )? ";" ;
valDecl              := "val" IDENTIFIER "=" expression ";" ;
```

#### Declaration helpers

```
function             := IDENTIFIER "(" parameters? ")" block ;
parameters           := IDENTIFIER ( "," IDENTIFIER )* ;
```

### Statements

```
statement            := exprStmt | ifStmt | returnStmt | whileStmt | block ;
exprStmt             := expression ";" ;
forStmt
ifStmt               := "if" "(" expression ")" statement ( "else" statement )? ;
returnStmt           := "return" expression? ";" ;
whileStmt            := "while" "(" expression ")" statement ;
block                := "{" declaration* "}" ;
```

### Expressions

```
expression           := assignment ;
assignment           := ( call ".")? IDENTIFIER "=" assignment | nullish_coalescence ;
nullish_coalescence  := logic_or ( "??" logic_or )* ;
logic_or             := logic_and ( "||" logic_and )* ;
logic_and            := equality ( "&&" equality )* ;
equality             := comparison ( ( "!=" | "==" ) comparison )* ;
comparison           := term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term                 := factor ( ( "-" | "+" ) factor )* ;
factor               := unary ( ( "/" | "*" ) unary )* ;
unary                := ( "!" | "-" ) unary | call ;
call                 := primary ( "(" arguments? ")" ) | "." IDENTIFIER )* ;
primary              := "false" | "true" | "null" | NUMBER | STRING | "(" expression ")" | IDENTIFIER ;
```

#### Expression Helpers

```
arguments            := expression ( "," expression )* ;
```

### Terminals

```
STRING               := \".*\"
NUMBER               := [1-9][0-9]*(\.[0-9]+)?
IDENTIFIER           := [a-zA-Z_][a-zA-Z0-9_]*
EOF                  := special for end of the file
```

### Keywords

```
# declarations
"class" | "fun" | "var" | "val"

# statements
"else" | "for" | "if" | "while"

# values
"false" | "null" | "true"

# etc
"and" | "or" | "return"
```

## Download

### Gradle + Jitpack:

```kotlin
repositories {
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.iamdudeman:sola-script:0.1.0")
}
```

[sola-script jar downloads](https://github.com/iamdudeman/sola-script/releases) hosted on GitHub releases.
