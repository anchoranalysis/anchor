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

package org.anchoranalysis.bean.xml.exception;

import java.nio.file.NoSuchFileException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * Converts certain exceptions that occur from XML-Parsing into a friendly form to display to the
 * user
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HelperFriendlyExceptions {

    /**
     * Possibly replaces an exception with a more user-friendly description
     *
     * @param e an exception that will be possibly replaced
     * @return either the exception e, or an alternative more user-friendly description
     */
    public static Throwable maybeCreateUserFriendlyException(Throwable e) {
        if ((e instanceof ConfigurationRuntimeException) || (e instanceof ConfigurationException)) {
            // If it's nested ConfigurationRuntimeExceptions, then we get the underlying cause
            if (e.getCause() != null) {
                return maybeCreateUserFriendlyException(e.getCause());
            } else {
                return new BeanXmlException(e);
            }
        } else if (e instanceof ClassNotFoundException) {
            // For ClassNotFoundException, we create a nicer to read read message
            ClassNotFoundException eCast = (ClassNotFoundException) e;
            return new BeanXmlException(
                    String.format(
                            "Class '%s' is referred to in BeanXML but cannot be found on classpath.%nPlease check spelling of config-class attributes, and make sure relevant plugins are installed.",
                            eCast.getMessage()));
        } else if (e instanceof NoClassDefFoundError) {
            // For NoClassDefFoundError, we create a nicer to read read message
            NoClassDefFoundError eCast = (NoClassDefFoundError) e;
            return new BeanXmlException(
                    String.format(
                            "Class '%s' is referred to in BeanXML but cannot be found on classpath.%nPlease check spelling of config-class attributes, and make sure relevant plugins are installed.",
                            eCast.getMessage()));
        } else if (e instanceof NoSuchFileException) {
            // For NoClassDefFoundError, we create a nicer to read read message
            NoSuchFileException eCast = (NoSuchFileException) e;
            return new BeanXmlException(
                    String.format("Cannot find included file: %s", eCast.getMessage()));
        }
        return e;
    }
}
