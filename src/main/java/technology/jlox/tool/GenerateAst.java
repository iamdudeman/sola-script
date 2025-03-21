package technology.jlox.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("Usage: generate_ast <output_directory>");
      System.out.println("Using default output directory [src/main/java/technology/jlox]");
    }

    String outputDirectory = args.length == 0 ? "src/main/java/technology/jlox" : args[0];

    defineAst(outputDirectory, "Expr", Arrays.asList(
      "Assign    : Token name, Expr value",
      "Binary    : Expr left, Token operator, Expr right",
      "Call      : Expr callee, Token paren, List<Expr> arguments",
      "Grouping  : Expr expression",
      "Literal   : Object value",
      "Logical   : Expr left, Token operator, Expr right",
      "Unary     : Token operator, Expr right",
      "Variable  : Token name"
      ));

    defineAst(outputDirectory, "Stmt", Arrays.asList(
      "Block       : List<Stmt> statements",
      "Class       : Token name, List<Stmt.Function> methods",
      "Expression  : Expr expression",
      "Function    : Token name, List<Token> params, List<Stmt> body",
      "If          : Expr condition, Stmt thenBranch, Stmt elseBranch",
      "Print       : Expr expression",
      "Return      : Token keyword, Expr value",
      "Var         : Token name, Expr initializer",
      "While       : Expr condition, Stmt body"
    ));
  }

  private static void defineAst(String outputDirectory, String baseName, List<String> types) throws IOException {
    String path = outputDirectory + "/" + baseName + ".java";
    PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

    writer.println("package technology.jlox;");
    writer.println();
    writer.println("import java.util.List;");
    writer.println();
    writer.println("abstract class " + baseName + " {");

    defineVisitor(writer, baseName, types);

    for (String type : types) {
      String className = type.split(":")[0].trim();
      String fields = type.split(":")[1].trim();

      defineType(writer, baseName, className, fields);
    }

    // The base accept() method
    writer.println();
    writer.println("  abstract <R> R accept(Visitor<R> visitor);");

    writer.println("}");
    writer.println();

    writer.close();
  }

  private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
    writer.println("  interface Visitor<R> {");

    for (String type : types) {
      String typeName = type.split(":")[0].trim();

      writer.println("    R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
    }

    writer.println("  }");
  }

  private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
    writer.println("static class " + className + " extends " + baseName + " {");

    // constructor
    writer.println("  " + className + " (" + fieldList + ") {");

    // parameters
    String[] fields = fieldList.split(", ");

    for (String field : fields) {
      String name = field.split(" ")[1];

      writer.println("    this." + name + " = " + name + ";");
    }

    writer.println("  }");

    // visitor pattern
    writer.println();
    writer.println("  @Override");
    writer.println("  <R> R accept(Visitor<R> visitor) {");
    writer.println("    return visitor.visit" + className + baseName + "(this);");
    writer.println("  }");

    // Fields
    writer.println();

    for (String field : fields) {
      writer.println("  final " + field + ";");
    }

    writer.println("}");
  }
}
