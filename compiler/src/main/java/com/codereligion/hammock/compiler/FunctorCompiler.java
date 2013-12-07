package com.codereligion.hammock.compiler;

import com.codereligion.hammock.compiler.model.Name;
import com.codereligion.hammock.compiler.model.Type;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("com.codereligion.hammock.Functor")
public class FunctorCompiler extends AbstractProcessor {

    private final Map<ElementKind, Parser> parsers = ImmutableMap.<ElementKind, Parser>of(
            ElementKind.METHOD, new MethodParser()
    );

    private final CacheLoader<TypeElement, Type> loader = new CacheLoader<TypeElement, Type>() {

        @Override
        public Type load(TypeElement element) throws Exception {
            return new Type(new Name(element.getQualifiedName() + "_"));
        }

    };

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean claimed = false;

        final LoadingCache<TypeElement, Type> cache = CacheBuilder.newBuilder().build(loader);

        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                try {
                    parse(element, cache);
                    claimed = true;
                } catch (UnsupportedUsageException e) {
                    error(e.getElement(), e.getMessage());
                }
            }
        }

        write(cache);

        return claimed;
    }

    private void parse(Element element, Function<TypeElement, Type> storage) throws UnsupportedUsageException {
        final ElementKind kind = element.getKind();

        final Parser parser = parsers.get(kind);

        if (parser == null) {
            throw new UnsupportedUsageException(element, "unsupported usage");
        }

        parser.check(element);
        parser.parse(element, storage);
    }

    private void write(LoadingCache<TypeElement, Type> cache) {
        for (Type type : cache.asMap().values()) {
            write(type);
        }
    }

    private void write(Type type) {
        final Thread thread = Thread.currentThread();
        final ClassLoader original = thread.getContextClassLoader();

        try {
            final ClassLoader loader = FunctorCompiler.class.getClassLoader();
            thread.setContextClassLoader(loader);

            final MustacheFactory factory = new DefaultMustacheFactory();
            final Mustache mustache = factory.compile("templates/template.mustache");

            try {
                final Filer filer = processingEnv.getFiler();
                final JavaFileObject file = filer.createSourceFile(type.getName().getQualified());

                try (Writer writer = file.openWriter()) {
                    mustache.execute(writer, type).flush();
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        } finally {
            thread.setContextClassLoader(original);
        }
    }

    private void error(Element element, String message) {
        processingEnv.getMessager().printMessage(Kind.ERROR, message, element);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
