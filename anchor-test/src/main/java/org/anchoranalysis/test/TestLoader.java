/*-
 * #%L
 * anchor-test
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

/**
 * Loads test data, which is found at some location on the file-system
 *
 * @author Owen Feehan
 */
public class TestLoader {

    /** Path to where the test-data is stored */
    private Path pathTestDataRoot;

    /** Makes a new test-data loader */
    private TestLoader(String root) {
        this(Paths.get(root));
    }

    /**
     * Makes a new test-data loader
     *
     * @param root path to where the test-data is stored
     */
    private TestLoader(Path root) {
        super();

        if (!root.toFile().exists()) {
            throw new TestDataInitException(String.format("Cannot find path '%s' path", root));
        }

        if (!root.toFile().isDirectory()) {
            throw new TestDataInitException(String.format("Path '%s' is not a folder", root));
        }

        this.pathTestDataRoot = root.toAbsolutePath();
    }

    /**
     * Creates a new test-data loader finding "src/test/resources" using the Maven working directory
     *
     * @return a testLoader associated with MAVEN_WORKING_DIR/src/test/resources/
     */
    public static TestLoader createFromMavenWorkingDirectory() {
        return new TestLoader("src/test/resources");
    }

    /**
     * Creates a new test-data loader finding "src/test/resources/PLUS_SOMETHING" using the Maven
     * working directory
     *
     * @param toAppendToDirectory appended to maven working dir to determine final directory
     * @return a testLoader associated with the MAVEN_WORKING_DIR/src/test/resources/PLUS_SOMETHING
     */
    public static TestLoader createFromMavenWorkingDirectory(String toAppendToDirectory) {
        Path path = Paths.get("src/test/resources").resolve(toAppendToDirectory);
        return new TestLoader(path.toString());
    }

    /**
     * Creates a new test-data loader using an explicit File path as root
     *
     * @param rootDirectory the path where the root folder is
     * @return a testLoader associated with the explicit root
     */
    public static TestLoader createFromExplicitDirectory(String rootDirectory) {
        return createFromExplicitDirectory(Paths.get(rootDirectory));
    }

    /**
     * Creates a new test-data loader using an explicit File path as root
     *
     * @param rootDirectory the path where the root folder is
     * @return a testLoader associated with the explicit root
     */
    public static TestLoader createFromExplicitDirectory(Path rootDirectory) {
        return new TestLoader(rootDirectory);
    }

    /**
     * Creates a new test-loader for a subdirectory of the current test
     *
     * @param subdirectory the subdirectory to use (relative path to the current root)
     * @return the new test-loader
     */
    public TestLoader createForSubdirectory(String subdirectory) {
        return new TestLoader(pathTestDataRoot.resolve(subdirectory));
    }

    /**
     * Resolves a path to test-data (relative path to the test-data root) to an absolute path on the
     * file system
     *
     * @param testPath relative-path of a test-data item. It is relative to the test-data root.
     * @return the resolved-path
     */
    public Path resolveTestPath(String testPath) {
        if (Paths.get(testPath).isAbsolute()) {
            throw new IllegalArgumentException(
                    String.format("testPath should be relative, not absolute: %s", testPath));
        }
        return pathTestDataRoot.resolve(testPath);
    }

    /**
     * Does a resource exist with a particular folderPath + fileName
     *
     * @param testFilePath path to a file in the test-data
     * @return true if a file is found at the location, false otherwise
     */
    public boolean doesPathExist(String testFilePath) {

        Path fileNameReslvd = resolveTestPath(testFilePath);

        return fileNameReslvd.toFile().exists();
    }

    /**
     * Does a resource exist with a particular folderPath + fileName
     *
     * @param testFolderPath path to a folder in the test-data (can be empty)
     * @param fileName a filename in the testFolderPath
     * @return true if a file is found at the location, false otherwise
     */
    public boolean doesPathExist(String testFolderPath, String fileName) {
        Path folder = resolveTestPath(testFolderPath);
        return folder.resolve(fileName).toFile().exists();
    }

    private void listDirectory(String dirPath, int level) {
        File dir = new File(dirPath);
        File[] firstLevelFiles = dir.listFiles();
        if (firstLevelFiles != null && firstLevelFiles.length > 0) {
            for (File aFile : firstLevelFiles) {
                for (int i = 0; i < level; i++) {
                    System.out.print("\t"); // NOSONAR
                }
                if (aFile.isDirectory()) {
                    System.out.println("[" + aFile.getName() + "]"); // NOSONAR
                    listDirectory(aFile.getAbsolutePath(), level + 1);
                } else {
                    System.out.println(aFile.getName()); // NOSONAR
                }
            }
        }
    }

    /**
     * Prints the names of all files (recursively) in a test-folder to stdout
     *
     * @param testFolderPath path to a folder in the test-data (can be empty)
     */
    public void printAllFilesFromTestFolderPath(String testFolderPath) {
        Path folderPathResolved = resolveTestPath(testFolderPath);
        listDirectory(folderPathResolved.toString(), 0);
    }

    /**
     * Opens a XML document - with a path relative to the test root
     *
     * @param testPath the path to the xml file (relative to the test root)
     * @return the XML document
     */
    public Document openXmlFromTestPath(String testPath) {
        return openXmlAbsoluteFilePath(resolveTestPath(testPath));
    }

    /**
     * Opens a XML document - with an absolute path on the filesystem
     *
     * @param filePath the path to the xml file (absolute path)
     * @return the XML document
     */
    public static Document openXmlAbsoluteFilePath(String filePath) {
        return openXmlAbsoluteFilePath(Paths.get(filePath));
    }

    /**
     * Opens a XML document - with an absolute path on the filesystem
     *
     * @param filePath the path to the xml file (absolute path)
     * @return the XML document
     */
    public static Document openXmlAbsoluteFilePath(Path filePath) {
        try {
            DocumentBuilderFactory dbf = createDocumentBuilderFactory();

            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(filePath.toFile());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new TestDataLoadException(e);
        }
    }

    /**
     * Does a check if the XML documents are equal
     *
     * <p>Note that both objects are normalized during the check, and their state changes
     * permanently.
     *
     * @param doc1 first document
     * @param doc2 second document
     * @return true if their contents match, false otherwise
     */
    public static boolean areXmlEqual(Document doc1, Document doc2) {
        return areXmlEqual(Input.fromDocument(doc1), Input.fromDocument(doc2));
    }

    /**
     * Copies all the data in the test-data folder (recursively), preserving file-dates
     *
     * @param dirDest destination-folder
     * @throws IOException if a copy error occurs
     */
    public void copyToDirectory(File dirDest) throws IOException {
        FileUtils.copyDirectory(pathTestDataRoot.toFile(), dirDest, true);
    }

    /**
     * Copies specific subdirectories from the test-data folder (recursively), preserving file-dates
     *
     * @param subdirectoriesSrc which subdirectories to copy from (their full-path is preserved)
     * @param dirDest destination-folder
     * @throws IOException if a copy error occurs
     */
    public void copyToDirectory(String[] subdirectoriesSrc, File dirDest) throws IOException {

        for (String subdir : subdirectoriesSrc) {
            Path pathSubdir = pathTestDataRoot.resolve(subdir);

            // Create the target folder
            File destSubdir = new File(dirDest, subdir);
            destSubdir.mkdirs();

            FileUtils.copyDirectory(pathSubdir.toFile(), destSubdir, true);
        }
    }

    public void testManifestExperiment(String outputDir) {
        assertTrue(doesPathExist(outputDir, "manifestExperiment.ser"));
        assertTrue(doesPathExist(outputDir, "manifestExperiment.ser.xml"));
        assertTrue(!doesPathExist(outputDir, "manifestExperiment2.ser.xml"));
    }

    public Path getRoot() {
        return pathTestDataRoot;
    }

    private static DocumentBuilderFactory createDocumentBuilderFactory()
            throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return dbf;
    }

    private static boolean areXmlEqual(Input.Builder expectedXML, Input.Builder actualXML) {
        Diff difference =
                DiffBuilder.compare(expectedXML)
                        .ignoreWhitespace()
                        .ignoreComments()
                        .withTest(actualXML)
                        .build();
        return !difference.hasDifferences();
    }
}
