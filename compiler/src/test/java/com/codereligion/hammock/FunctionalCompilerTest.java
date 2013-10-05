package com.codereligion.hammock;

import com.codereligion.hammock.compiler.FunctionalCompiler;
import com.codereligion.hammock.compiler.sample.InvalidUseOnNoArgumentStaticMethod;
import com.codereligion.hammock.compiler.sample.InvalidUseOnSingleArgumentInstanceMethod;
import com.codereligion.hammock.compiler.sample.InvalidUseOnTwoArgumentInstanceMethod;
import com.codereligion.hammock.compiler.sample.InvalidUseOnTwoArgumentStaticMethod;
import com.codereligion.hammock.compiler.sample.ValidUseOnAllInstanceMethods;
import com.codereligion.hammock.compiler.sample.ValidUseOnAllStaticMethods;
import com.codereligion.hammock.compiler.sample.ValidUseOnNoInstanceMethods;
import com.codereligion.hammock.compiler.sample.ValidUseOnNoStaticMethods;
import com.codereligion.hammock.compiler.sample.ValidUseOnSomeInstanceMethods;
import com.codereligion.hammock.compiler.sample.ValidUseOnSomeStaticMethods;
import org.junit.Test;

import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.io.IOException;

import static com.codereligion.hammock.DiagnosticMatchers.failedAtLine;
import static com.codereligion.hammock.DiagnosticMatchers.successful;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FunctionalCompilerTest {

    private final Processor unit = new FunctionalCompiler();
    private final ProcessorRunner runner = new ProcessorRunner();

    private DiagnosticCollector<JavaFileObject> compilationOf(Class<?> sample) throws IOException {
        return runner.compile(unit, sample);
    }

    @Test
    public void validUseOnAllInstanceMethods() throws IOException {
        assertThat(compilationOf(ValidUseOnAllInstanceMethods.class), is(successful()));
    }

    @Test
    public void validUseOnSomeInstanceMethods() throws IOException {
        assertThat(compilationOf(ValidUseOnSomeInstanceMethods.class), is(successful()));
    }

    @Test
    public void validUseOnNoInstanceMethods() throws IOException {
        assertThat(compilationOf(ValidUseOnNoInstanceMethods.class), is(successful()));
    }

    @Test
    public void invalidUseOnSingleArgumentInstanceMethod() throws IOException {
        assertThat(compilationOf(InvalidUseOnSingleArgumentInstanceMethod.class), failedAtLine(11));
    }

    @Test
    public void invalidUseOnTwoArgumentInstanceMethod() throws IOException {
        assertThat(compilationOf(InvalidUseOnTwoArgumentInstanceMethod.class), failedAtLine(12));
    }

    @Test
    public void validUseOnAllStaticMethods() throws IOException {
        assertThat(compilationOf(ValidUseOnAllStaticMethods.class), is(successful()));
    }

    @Test
    public void validUseOnSomeStaticMethods() throws IOException {
        assertThat(compilationOf(ValidUseOnSomeStaticMethods.class), is(successful()));
    }

    @Test
    public void validUseOnNoStaticMethods() throws IOException {
        assertThat(compilationOf(ValidUseOnNoStaticMethods.class), is(successful()));
    }

    @Test
    public void invalidUseOnNoArgumentStaticMethod() throws IOException {
        assertThat(compilationOf(InvalidUseOnNoArgumentStaticMethod.class), failedAtLine(8));
    }

    @Test
    public void invalidUseOnTwoArgumentStaticMethod() throws IOException {
        assertThat(compilationOf(InvalidUseOnTwoArgumentStaticMethod.class), failedAtLine(9));
    }

}
