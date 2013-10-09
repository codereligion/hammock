package com.codereligion.hammock.compiler;

import com.codereligion.hammock.compiler.playground.Member;
import com.codereligion.hammock.compiler.playground.Member_;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.squareup.javawriter.JavaWriter;
import difflib.DiffUtils;
import difflib.Patch;
import org.junit.Test;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static com.squareup.javawriter.JavaWriter.stringLiteral;
import static java.util.EnumSet.of;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.junit.Assert.assertEquals;

public class WriterTest {

    @Test
    public void test() throws IOException {
        final GeneratedClass generatedClass = new GeneratedClass();

        generatedClass.setPackageName(Member.class.getPackage().getName());
        generatedClass.setSimpleSourceName(Member.class.getSimpleName());

        final GeneratedFunction getName = new GeneratedFunction();
        getName.setName("getName");
        getName.setSimpleTargetName(String.class.getSimpleName());
        getName.setFullyQualifiedTargetName(String.class.getName());
        generatedClass.getMethods().add(getName);

        final GeneratedPredicate isHappy = new GeneratedPredicate();
        isHappy.setName("isHappy");
        generatedClass.getMethods().add(isHappy);
        
        final StringWriter writer = new StringWriter();

        final JavaWriter java = new JavaWriter(writer);
        java.setIndent("    ");

        java.emitPackage(generatedClass.getPackageName());
        if (!generatedClass.getImports().isEmpty()) {
            java.emitImports(generatedClass.getImports());
            java.emitEmptyLine();
        }
        if (generatedClass.hasFunctions()) {
            java.emitImports(Function.class.getName());
        }
        if (generatedClass.hasPredicates()) {
            java.emitImports(Predicate.class.getName());
        }
        java.emitEmptyLine();
        java.emitImports(Generated.class.getName());
        java.emitImports(Nullable.class.getName());
        java.emitEmptyLine();
        java.emitAnnotation(Generated.class, stringLiteral(FunctionalCompiler.class.getName()));
        java.beginType(generatedClass.getSimpleSourceName() + "_", "class", of(PUBLIC, FINAL));

        for (GeneratedFunction function : generatedClass.getFunctions()) {
            java.emitEmptyLine();
            final String type = "Function<" + generatedClass.getSimpleSourceName() + ", " + function.getSimpleTargetName() + ">";
            java.beginType(function.getCamelCaseName(), "enum", of(PRIVATE), null, type);
            java.emitEmptyLine();
            java.emitLastEnumValue("INSTANCE");
            java.emitEmptyLine();
            java.emitAnnotation(Nullable.class);
            java.emitAnnotation(Override.class);
            java.beginMethod(String.class.getCanonicalName(), "apply", of(PUBLIC), "@Nullable " + generatedClass.getSimpleSourceName(), "input");
            java.emitStatement("return input == null ? null : input.%s()", function.getName());
            java.endMethod();
            java.emitEmptyLine();
            java.endType();
        }
        
        for (GeneratedPredicate predicate : generatedClass.getPredicates()) {
            java.emitEmptyLine();
            final String type = "Predicate<" + generatedClass.getSimpleSourceName() + ">";
            java.beginType(predicate.getCamelCaseName(), "enum", of(PRIVATE), null, type);
            java.emitEmptyLine();
            java.emitLastEnumValue("INSTANCE");
            java.emitEmptyLine();
            java.emitAnnotation(Override.class);
            java.beginMethod(boolean.class.getCanonicalName(), "apply", of(PUBLIC), "@Nullable " + generatedClass.getSimpleSourceName(), "input");
            java.emitStatement("return input != null && input.%s()", predicate.getName());
            java.endMethod();
            java.emitEmptyLine();
            java.endType();
        }

        java.emitEmptyLine();

        java.beginMethod(null, generatedClass.getSimpleSourceName() + "_", of(PRIVATE));
        java.emitEmptyLine();
        java.endMethod();

        for (GeneratedFunction function : generatedClass.getFunctions()) {
            java.emitEmptyLine();
            final String type = "Function<" + generatedClass.getSimpleSourceName() + ", " + function.getSimpleTargetName() + ">";
            java.beginMethod(type, function.getName(), of(PUBLIC, STATIC));
            java.emitStatement("return %s.INSTANCE", function.getCamelCaseName());
            java.endMethod();
        }

        for (GeneratedPredicate predicate : generatedClass.getPredicates()) {
            java.emitEmptyLine();
            final String type = "Predicate<" + generatedClass.getSimpleSourceName() + ">";
            java.beginMethod(type, predicate.getName(), of(PUBLIC, STATIC));
            java.emitStatement("return %s.INSTANCE", predicate.getCamelCaseName());
            java.endMethod();
        }
        
        java.emitEmptyLine();
        java.endType();

        final String actual = writer.toString();
        final String path = Member_.class.getName().replace('.', '/') + ".java";
        final File file = new File("src/test/java", path);
        final String expected = Files.toString(file, Charsets.UTF_8);

        final Splitter splitter = Splitter.on('\n');
        final List<String> lines = splitter.splitToList(expected);
        final Patch<String> patch = DiffUtils.diff(lines, splitter.splitToList(actual));

        if (patch.getDeltas().isEmpty()) {
            return;
        }

        final List<String> list = DiffUtils.generateUnifiedDiff("Expected", "Actual", lines, patch, 0);
        throw new AssertionError('\n' + Joiner.on('\n').join(list));
    }

}
