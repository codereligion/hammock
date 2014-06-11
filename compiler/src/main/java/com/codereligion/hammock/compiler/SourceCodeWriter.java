package com.codereligion.hammock.compiler;

import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

import javax.annotation.processing.Filer;
import java.io.IOException;
import java.io.OutputStream;

import static java.lang.String.format;

final class SourceCodeWriter extends CodeWriter {

    private final Filer filer;
    private OutputStream stream;

    public SourceCodeWriter(Filer filer) {
        this.filer = filer;
    }

    @Override
    public OutputStream openBinary(JPackage jPackage, String fileName) throws IOException {
        final String nameWithoutExtension = Files.getNameWithoutExtension(fileName);
        final String typeName = format("%s.%s", jPackage.name(), nameWithoutExtension.replace('.', '$'));
        this.stream = filer.createSourceFile(typeName).openOutputStream();
        return stream;
    }

    @Override
    public void close() throws IOException {
        Closeables.close(stream, false);
    }
    
}
