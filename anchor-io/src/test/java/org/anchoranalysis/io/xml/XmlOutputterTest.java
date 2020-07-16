/* (C)2020 */
package org.anchoranalysis.io.xml;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.anchoranalysis.test.TestLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlOutputterTest {

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void test()
            throws ParserConfigurationException, SAXException, IOException, TransformerException,
                    URISyntaxException {
        String testPathIn = "simpleXML01.xml";
        Path pathOut = folder.newFile("a file name with_spaces_and_underscores_.xml").toPath();

        TestLoader loader = TestLoader.createFromMavenWorkingDirectory();

        Document docIn = loader.openXmlFromTestPath(testPathIn);

        XmlOutputter.writeXmlToFile(docIn, pathOut);

        Document docOut = TestLoader.openXmlAbsoluteFilePath(pathOut);

        assertTrue(TestLoader.areXmlEqual(docIn, docOut));
    }
}
