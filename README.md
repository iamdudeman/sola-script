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


## Sola grammar

```
program         := declaration* EOF ;
```

### Declarations

```
declaration     := todo
```

### Statements

```
statement       := todo
```

### Expressions

```
expression      := call
call            := primary ( "(" arguments? ")" ) | "." IDENTIFIER )* ;
primary         := "false" | "true" | "null" | NUMBER | STRING | "(" expression ")" | IDENTIFIER | "this" | "super" "." IDENTIFIER ;
```

#### Expression Helpers

```
arguments       := expression ( "," expression )* ;
```

### Terminals

```
STRING          := \".*\"
NUMBER          := [1-9][0-9]*(\.[0-9]+)?
IDENTIFIER      := [a-zA-Z_][a-zA-Z0-9_]*
EOF             := special for end of the file
```

### Keywords

```
# declarations
"class" | "fun" | "var" | "val"

# statements
"else" | "for" | "if" | "while"

# values
"false" | "null" | "super" | "this" | "true"

# etc
"and" | "or" | "return"
```
