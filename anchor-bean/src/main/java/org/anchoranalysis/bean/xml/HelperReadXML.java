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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import javax.xml.parsers.ParserConfigurationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.xml.exception.BeanXMLException;
import org.anchoranalysis.core.serialize.XMLParser;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.xml.sax.SAXParseException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperReadXML {

    /**
     * Reads an XML file from the filesystem, and wraps it as BeanXML
     *
     * @param filePath path of the file to read
     * @return the wrapped XML file
     * @throws BeanXMLException if something goes wrong, and either the file cannot be read, or the
     *     XML does not conform to Bean standards
     */
    public static XMLConfiguration readBeanXMLFromFilesystem(Path filePath)
            throws BeanXMLException {

        try {
            File configFile = filePath.toFile();

            return readBeanXMLFromStream(new FileInputStream(configFile));
        } catch (FileNotFoundException e) {
            // This also tries to cach files that cannot be included
            throw createIncludedFileFailedException(e.getMessage());
        }
    }

    /**
     * Reads an XML file from a path (either on the filesystem, or a resource-file)
     *
     * @param source the stream to read the BeanXML from
     * @return the wrapped XML file
     * @throws BeanXMLException if something goes wrong, and either the file cannot be read, or the
     *     XML does not conform to Bean standards
     */
    private static XMLConfiguration readBeanXMLFromStream(InputStream source)
            throws BeanXMLException {

        try {
            // We initialize a DocumentBuilder specifically, so that we can switch off
            //  the errorHandler as otherwise it prints ugly error messages to the screen
            XMLConfiguration config = new XMLConfiguration();
            config.setDocumentBuilder(XMLParser.createBuilderWithDefaultErrorHandler());
            config.load(source);
            return config;

        } catch (ConfigurationException e) {
            throw convertConfigurationException(e);
        } catch (ParserConfigurationException e) {
            throw new BeanXMLException(
                    "An error occurred creating the DocumentBuilder for reading xml", e);
        }
    }

    /**
     * Converts a ConfigurationException into BeanXMLException, looking for some particular common
     * exceptions to trap and change
     *
     * @param exc exception to convert
     * @return converted-exception
     */
    private static BeanXMLException convertConfigurationException(ConfigurationException exc) {

        // If the cause is a SAXParseException we promote it, and ignore the ConfigurationException
        // (for user-friendly error output)
        if (exc.getCause() instanceof SAXParseException causeCast) {
            return new BeanXMLException(locationOfXMLError(causeCast), exc.getCause());
        }

        // if the cause is a not-found file error we simplify it
        // The two exceptions cover changes with Java 7
        // * With Java 7, NoSuchFileException refers specifically to files that are missing
        // * Before Java 7, FileNotFoundException was used for this error, and also for errors but
        // cannot be accessed
        //
        // As it varies a bit which gets thrown by implementation, we catch both here
        if (exc.getCause() instanceof FileNotFoundException
                || exc.getCause() instanceof NoSuchFileException) {
            return createIncludedFileFailedException(exc.getCause().getMessage());
        }
        return new BeanXMLException(exc);
    }

    /**
     * Creates an exception representing the failure to include another file
     *
     * @param filePath the filePath which failed when being included
     * @return the new exception
     */
    private static BeanXMLException createIncludedFileFailedException(String filePath) {
        return new BeanXMLException(String.format("Cannot find included file: %s", filePath));
    }

    /**
     * Makes a string describing the location of a parsign error
     *
     * @param e parsing-exception
     * @return string of format "At Line=%d and Column=%d" describing where the parsing-error
     *     occurred
     */
    private static String locationOfXMLError(SAXParseException e) {
        return String.format("At Line=%d Column=%d", e.getLineNumber(), e.getColumnNumber());
    }
}
