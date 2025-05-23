/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.io.output.xml;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.anchoranalysis.test.CompareXML;
import org.anchoranalysis.test.TestLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Outputs a XML file to the file-system and reopens-it, checking equality.
 *
 * @author Owen Feehan
 */
class XMLOutputterTest {

    /** The directory in which the test creates files. */
    @TempDir Path directory;

    /**
     * Checks outputting a XML file.
     *
     * @throws ParserConfigurationException if thrown by underlying code.
     * @throws SAXException if thrown by underlying code.
     * @throws IOException if thrown by underlying code.
     * @throws TransformerException if thrown by underlying code.
     * @throws URISyntaxException if thrown by underlying code.
     */
    @Test
    void test()
            throws ParserConfigurationException,
                    SAXException,
                    IOException,
                    TransformerException,
                    URISyntaxException {
        String testPathIn = "simpleXML01.xml";
        Path pathOut = directory.resolve("a file name with_spaces_and_underscores_.xml");

        TestLoader loader = TestLoader.createFromMavenWorkingDirectory();

        Document docIn = loader.openXmlFromTestPath(testPathIn);

        XMLWriter.writeXMLToFile(docIn, pathOut);

        Document docOut = TestLoader.openXmlAbsoluteFilePath(pathOut);

        assertTrue(CompareXML.areDocumentsEqual(docIn, docOut));
    }
}
