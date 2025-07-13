package com.sabyabhoi.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GenerateAst {

    private static final int USAGE_ERROR_STATUS_CODE = 64;

    public static void main(String[] args) throws IOException {
        if(args.length != 1) {
            System.err.println("Usage: generate_ast <output_directory>");
            System.exit(USAGE_ERROR_STATUS_CODE);
        }

        String outputDir = args[0];

        defineAst(outputDir, "Expr", List.of(
                "Binary: Expr left, Token operator, Expr right",
                "Grouping: Expr expression",
                "Literal: Object value",
                "Unary: Token operator, Expr right"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        try(PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8)) {
            writer.println("package com.sabyabhoi.lox;");
            writer.println();
            writer.println("import java.util.List;");
            writer.println();
            writer.println("abstract class " + baseName + " {");

            defineVisitor(writer, baseName, types);

            for(String type: types) {
                String className = type.split(":")[0].trim();
                String fields = type.split(":")[1].trim();
                defineType(writer, baseName, className, fields);
            }

            writer.println();
            writer.println("\tabstract <R> R accept(Visitor<R> visitor);");

            writer.println("}");
        }
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("\tinterface Visitor<R> {");

        for(String type : types) {
            String className = type.split(":")[0].trim();
            String methodDefinition = String.format("\t\tR visit%s%s(%s %s);", className, baseName, className, baseName.toLowerCase());
            writer.println(methodDefinition);
        }
        writer.println("\t}");
    }

    private static void defineType(
            PrintWriter writer, String baseName,
            String className, String fieldList) {
        writer.println("  static class " + className + " extends " +
                baseName + " {");

        // Constructor.
        writer.println("    " + className + "(" + fieldList + ") {");

        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("      this." + name + " = " + name + ";");
        }

        writer.println("    }");

        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("      return visitor.visit" +
                className + baseName + "(this);");
        writer.println("    }");


        // Fields.
        writer.println();
        for (String field : fields) {
            writer.println("    final " + field + ";");
        }

        writer.println("  }");
    }
}
