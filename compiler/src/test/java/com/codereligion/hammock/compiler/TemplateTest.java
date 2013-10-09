package com.codereligion.hammock.compiler;

import com.codereligion.hammock.compiler.playground.Member;
import com.codereligion.hammock.compiler.playground.Member_;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import difflib.DiffUtils;
import difflib.Patch;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class TemplateTest {

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

        final MustacheFactory factory = new DefaultMustacheFactory();
        final Mustache mustache = factory.compile("templates/template.mustache");

        final StringWriter writer = new StringWriter();
        mustache.execute(writer, generatedClass).flush();

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
