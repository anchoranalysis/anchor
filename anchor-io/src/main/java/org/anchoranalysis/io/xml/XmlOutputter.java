/* (C)2020 */
package org.anchoranalysis.io.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public class XmlOutputter {

    private XmlOutputter() {}

    public static void writeXmlToFile(Document doc, Path filePath)
            throws TransformerException, IOException {

        Transformer trans = createTransformer();

        File file = filePath.toFile();

        FileWriter fw = new FileWriter(file);
        StreamResult result = new StreamResult(fw);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
    }

    private static Transformer createTransformer() throws TransformerConfigurationException {
        TransformerFactory transFac = createFactory();

        Transformer trans = transFac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        return trans;
    }

    private static TransformerFactory createFactory() throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return factory;
    }
}
