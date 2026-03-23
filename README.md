# Crafting Interpreters - JLox Interpreter

This is the project I've been doing while following along with the book _Crafting Interpreters_ by Robert Nystrom. I've mostly been following the book as-is, except I've added Maven and nullchecking with JSpecify.

This is an interpreter and AST-Printer of the lox language written in Java.

## Building
If Java and Maven are installed, run `cd jlox/jlox` then run `mvn package`.

## Running
To run the compiled program, run `java -cp target/jlox-1.0-SNAPSHOT.jar com.samfoucart.jlox.Jlox src/main/resources/sample.lox`, or create a new lox file and pass that as the first argument.

As of right now, the program prints the AST then runs the program. Basic expressions, if-else blocks, and while loops are supported.
