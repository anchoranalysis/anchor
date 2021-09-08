/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.core.serialize;

import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Utility functions for parsing XML files using standard Java APIs.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XMLParser {

    /**
     * Creates a document-builder with default error-handling.
     *
     * @return a document-builder with default-error-handling.
     * @throws ParserConfigurationException if a {@link DocumentBuilder} cannot be created which
     *     satisfies the configuration requested.
     */
    public static DocumentBuilder createBuilderWithDefaultErrorHandler()
            throws ParserConfigurationException {
        DocumentBuilder builder = XMLParser.createDocumentBuilder();
        builder.setErrorHandler(new DefaultHandler());
        return builder;
    }

    /**
     * Creates a {@link TransformerFactory} that ignores any DTDs in the document and disables
     * external entities (to prevent XXE attacks).
     *
     * @return a newly created {@link DocumentBuilderFactory}
     * @throws TransformerConfigurationException if thrown by {@link TransformerFactory#setFeature}.
     */
    public static TransformerFactory createTransformerFactory()
            throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance(); // NOSONAR
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return factory;
    }

    /**
     * Parses a XML file to produce a {@link Document}.
     *
     * @param file the XML file to parse.
     * @return the parsed document
     * @throws SAXException if any parse errors occur.
     * @throws IOException if any IO errors occur.
     * @throws ParserConfigurationException if a {@link DocumentBuilder} cannot be created which
     *     satisfies the configuration requested.
     */
    public static Document parse(File file)
            throws SAXException, IOException, ParserConfigurationException {
        return createDocumentBuilder().parse(file);
    }

    /**
     * Creates a new XML document.
     *
     * @return the newly created {@link Document}
     * @throws ParserConfigurationException if a {@link DocumentBuilder} cannot be created which
     *     satisfies the configuration requested.
     */
    public static Document createNewDocument() throws ParserConfigurationException {
        return createDocumentBuilder().newDocument();
    }

    /**
     * Creates a {@link DocumentBuilder} that ignores any DTDs in the document and disables external
     * entities (to prevent XXE attacks).
     *
     * @return a newly created {@link DocumentBuilder}
     * @throws ParserConfigurationException if a {@link DocumentBuilder} cannot be created which
     *     satisfies the configuration requested.
     */
    private static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        return createDocumentBuilderFactory().newDocumentBuilder(); // NOSONAR
    }

    /**
     * Creates a {@link DocumentBuilderFactory} that ignores any DTDs in the document and disables
     * external entities (to prevent XXE attacks).
     *
     * @return a newly created {@link DocumentBuilderFactory}
     */
    private static DocumentBuilderFactory createDocumentBuilderFactory() {
        return DocumentBuilderFactory.newInstance(); // NOSONAR
    }
}
