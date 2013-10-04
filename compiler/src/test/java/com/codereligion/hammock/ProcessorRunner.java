package com.codereligion.hammock;

import com.google.common.base.Function;
import com.google.gag.annotation.remark.Booyah;
import com.google.gag.annotation.remark.Hack;

import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

final class ProcessorRunner {

    private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    private final Function<Class<?>, File> toResource = new Function<Class<?>, File>() {

        private final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        @Override
        public File apply(Class<?> input) {
            final String name = input.getName().replace('.', '/') + ".java";
            final URL url = loader.getResource(name);

            if (url == null) {
                throw sneakyThrow(new FileNotFoundException(name));
            }

            try {
                return new File(url.toURI().getSchemeSpecificPart());
            } catch (URISyntaxException ex) {
                return new File(url.getFile());
            }
        }

    };

    /**
     * Throws any checked exception without the need to declare it in the
     * throws clause.
     *
     * @param throwable the throwable to throw
     * @return never, this method <strong>always</strong> throws an exception
     * @see <a href="http://blog.jayway.com/2010/01/29/sneaky-throw">blog.jayway.com/2010/01/29/sneaky-throw</a>
     */
    @Hack
    @Booyah
    private static RuntimeException sneakyThrow(Throwable throwable) {
        ProcessorRunner.<RuntimeException>doSneakyThrow(throwable);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void doSneakyThrow(Throwable throwable) throws T {
        throw (T) checkNotNull(throwable, "Throwable");
    }

    public DiagnosticCollector<JavaFileObject> compile(Processor processor, Class<?>... classes)
        throws IOException {

        final Iterable<File> files = transform(asList(classes), toResource);
        final DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

        try (StandardJavaFileManager manager = compiler.getStandardFileManager(collector, null, null)) {
            final List<String> options = asList("-proc:only");
            final Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(files);
            final CompilationTask task = compiler.getTask(null, manager, collector, options, null, units);

            task.setProcessors(singleton(processor));
            task.call();
        }

        return collector;
    }

}
