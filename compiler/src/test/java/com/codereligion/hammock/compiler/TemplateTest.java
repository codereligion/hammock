package com.codereligion.hammock.compiler;

import com.codereligion.hammock.compiler.model.api.Closure;
import com.codereligion.hammock.compiler.model.api.Type;
import com.codereligion.hammock.compiler.model.simple.BaseClosure;
import com.codereligion.hammock.compiler.model.simple.SimpleType;
import com.codereligion.hammock.compiler.model.simple.StringClosureName;
import com.codereligion.hammock.compiler.model.simple.StringName;
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

public class TemplateTest {

    @Test
    public void test() throws IOException {
        final StringName member = new StringName(Member.class.getName());
        final Type type = new SimpleType(new StringName(Member.class.getName() + "_"));

        final StringName string = new StringName(String.class.getName());
        
        final Closure getName = new BaseClosure(new StringClosureName("getName"), member, string, true);
        type.getClosures().add(getName);

        final Closure getNickName = new BaseClosure(new StringClosureName("getNickName"), member, string, false);
        type.getClosures().add(getNickName);
        
        final Closure isHappy = new BaseClosure(new StringClosureName("isHappy"), member, true);
        type.getClosures().add(isHappy);
        
        final Closure isActive = new BaseClosure(new StringClosureName("isActive"), member, false);
        type.getClosures().add(isActive);

        final MustacheFactory factory = new DefaultMustacheFactory();
        final Mustache mustache = factory.compile("templates/template.mustache");

        final StringWriter writer = new StringWriter();
        mustache.execute(writer, type).flush();

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
