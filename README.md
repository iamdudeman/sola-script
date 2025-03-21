# sola-script

todo description

[![Java CI](https://github.com/iamdudeman/sola-script/actions/workflows/ci_build.yml/badge.svg)](https://github.com/iamdudeman/sola-script/actions/workflows/ci_build.yml)
[![Javadocs Link](https://img.shields.io/badge/Javadocs-blue.svg)](https://iamdudeman.github.io/sola-script/)
[![](https://jitpack.io/v/iamdudeman/sola-script.svg)](https://jitpack.io/#iamdudeman/sola-script)

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


## Grammar

### Rules

```
program      := declaration* EOF ;

declaration  := classDecl | funDecl | varDecl | statement ;
classDecl    := "class" IDENTIFIER "(" function* "} ;
funDecl      := "fun" function ;
function     := IDENTIFIER "(" parameters? ")" block ;
parameters   := IDENTIFIER ( "," IDENTIFIER )* ;
varDecl      := "var" IDENTIFIER ( "=" expression )? ";" ;

statement    := exprStmt | forStmt | ifStmt | printStmt | returnStmt | whileStmt | block
exprStmt     := expression ";" ;
forStmt      := "for" "(" ( varDecl | exprStmt | ";" ) expression? ";" expression? ")" statement ;
ifStmt       := "if" "(" expression ")" statement ( "else" statement )? ;
printStmt    := "print" expression ";" ;
returnStmt   := "return" expression? ";" ;
whileStmt    := "while" "(" expression ")" statement ;
block        := "{" declaration* "}"

expression   := assignment ;
assignment   := IDENTIFIER "=" assignment | logic_or ;
logic_or     := logic_and ( "or" logic_and )* ;
logic_and    := equality ( "and" equality )* ;
equality     := comparison ( ( "!=" | "==" ) comparison )* ;
comparison   := term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term         := factor ( ( "-" | "+" ) factor )* ;
factor       := unary ( ( "/" | "*" ) unary )*
unary        := ( "!" | "-" ) unary | call ;
call         := primary ( "(" arguments? ")" )* ;
arguments    := expression ( "," expression )* ;
primary      := NUMBER | STRING | BOOLEAN | NIL | "(" expression ")" | IDENTIFIER ;
```

### Terminals

```
STRING      := \".*\"
NUMBER      := [1-9][0-9]*(\.[0-9]+)?
BOOLEAN     := "true" | "false"
NIL         := "nil"
IDENTIFIER  :=
EOF         := // end of file
```
