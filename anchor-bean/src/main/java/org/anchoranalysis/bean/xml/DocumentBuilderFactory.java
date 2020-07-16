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
