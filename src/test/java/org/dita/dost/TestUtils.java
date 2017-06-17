/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2011 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */
package org.dita.dost;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.sax.SAXSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Test utilities.
 * 
 * @author Jarno Elovirta
 */
public class TestUtils {

    public static final File testStub = new File("src" + File.separator + "test" + File.separator + "resources");
    
    /**
     * Get test resource directory
     * 
     * @param testClass test class
     * @return resource directory
     * @throws RuntimeException if retrieving the directory failed
     */
    public static File getResourceDir(final Class<?> testClass) throws RuntimeException {
        final URL dir = ClassLoader.getSystemResource(testClass.getSimpleName());
        if (dir == null) {
            throw new RuntimeException("Failed to find resource for " + testClass.getSimpleName());
        }
        try {
            return new File(dir.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to find resource for " + testClass.getSimpleName() + ":" + e.getMessage(), e);
        }
    }
    
    /**
     * Create temporary directory based on test class.
     * 
     * @param testClass test class
     * @return temporary directory
     * @throws IOException if creating directory failed
     */
    public static File createTempDir(final Class<?> testClass) throws IOException {
        final File tempDir = new File(System.getProperty("java.io.tmpdir"),
                testClass.getName());
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new IOException("Unable to create temporary directory " + tempDir.getAbsolutePath());
        }
        return tempDir;
    }

    /**
     * Deletes a file. If file is a directory, delete it and all sub-directories.
     * 
     * @param file file or directory to delete, must not be null
     * @throws IOException in case deletion is unsuccessful
     */
    public static void forceDelete(final File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                for (final File c: file.listFiles()) {
                    forceDelete(c);
                }
            }
            if (!file.delete()) {
                throw new IOException("Failed to delete " + file.getAbsolutePath());
            }
        }
    }

    public static void assertHtmlEqual(InputSource exp, InputSource act) {
        final Diff d = DiffBuilder
                .compare(new SAXSource(exp))
                .withDocumentBuilderFactory(new HTMLDocumentBuilderFactory())
                .withTest(new SAXSource(act))
                .ignoreWhitespace()
                .ignoreComments()
                .normalizeWhitespace()
                .withNodeFilter(node -> node.getNodeType() != Node.PROCESSING_INSTRUCTION_NODE)
                .build();
        if (d.hasDifferences()) {
            throw new AssertionError(d.toString());
        }
    }

    private static class HTMLDocumentBuilderFactory extends DocumentBuilderFactory {
        @Override
        public Object getAttribute(final String arg0) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean getFeature(final String arg0) throws ParserConfigurationException {
            throw new UnsupportedOperationException();
        }
        @Override
        public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
            return new HtmlDocumentBuilder();
        }
        @Override
        public void setAttribute(final String arg0, final Object arg1) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }
        @Override
        public void setFeature(final String arg0, final boolean arg1) throws ParserConfigurationException {
            throw new UnsupportedOperationException();
        }
    }

}
