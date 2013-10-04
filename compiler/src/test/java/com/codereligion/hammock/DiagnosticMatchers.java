package com.codereligion.hammock;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

final class DiagnosticMatchers {

    public static Matcher<DiagnosticCollector<JavaFileObject>> successful() {
        return new BaseMatcher<DiagnosticCollector<JavaFileObject>>() {

            @Override
            public boolean matches(Object item) {
                @SuppressWarnings("unchecked")
                final DiagnosticCollector<JavaFileObject> collector = (DiagnosticCollector<JavaFileObject>) item;

                return Iterables.all(collector.getDiagnostics(), new Predicate<Diagnostic<? extends JavaFileObject>>() {
                    @Override
                    public boolean apply(Diagnostic<? extends JavaFileObject> input) {
                        return input.getKind() != Kind.ERROR;
                    }
                });
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("successful");
            }

        };
    }

    public static Matcher<DiagnosticCollector<JavaFileObject>> failedAtLine(final long lineNumber) {
        return new BaseMatcher<DiagnosticCollector<JavaFileObject>>() {
            @Override
            public boolean matches(Object item) {
                @SuppressWarnings("unchecked")
                final DiagnosticCollector<JavaFileObject> collector = (DiagnosticCollector<JavaFileObject>) item;

                return Iterables.any(collector.getDiagnostics(), new Predicate<Diagnostic<? extends JavaFileObject>>() {

                    @Override
                    public boolean apply(Diagnostic<? extends JavaFileObject> input) {
                        return input.getKind() == Kind.ERROR && input.getLineNumber() == lineNumber;
                    }

                });
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("failed at line ").appendValue(lineNumber);
            }

        };
    }

}
