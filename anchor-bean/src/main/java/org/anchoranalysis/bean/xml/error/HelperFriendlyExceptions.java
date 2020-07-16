/* (C)2020 */
package org.anchoranalysis.bean.xml.error;

import java.nio.file.NoSuchFileException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * Converts certain exceptions that occur from XML-Parsing into a friendly form to display to the
 * user
 *
 * @author Owen Feehan
 */
public class HelperFriendlyExceptions {

    private HelperFriendlyExceptions() {}

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
