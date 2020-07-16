/* (C)2020 */
package org.anchoranalysis.bean.xml;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.DefaultHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DocumentBuilderHelper {

    /**
     * Creates a document-builder with default error-handling
     *
     * @return a document-builder with default-error-handling
     * @throws ParserConfigurationException if the exception is thrown from
     *     DocumentBuilder.newDocumentBuilder
     */
    public static DocumentBuilder createFactory() throws ParserConfigurationException {

        DocumentBuilderFactory dbf = createDocumentBuilderFactory();
        return createDocumentBuilderWithHandler(dbf, new DefaultHandler());
    }

    private static DocumentBuilder createDocumentBuilderWithHandler(
            DocumentBuilderFactory dbf, ErrorHandler eh) throws ParserConfigurationException {
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setErrorHandler(eh);
        return db;
    }

    private static DocumentBuilderFactory createDocumentBuilderFactory()
            throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // Ignores any DTDs in the document
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return dbf;
    }
}
