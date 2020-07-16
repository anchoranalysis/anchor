/* (C)2020 */
package org.anchoranalysis.bean.xml.error;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

/**
 * An exception that occurs when one cannot process BeanXML for some reason
 *
 * @author Owen Feehan
 */
public class BeanXmlException extends AnchorFriendlyCheckedException {

    /** Default serialization */
    private static final long serialVersionUID = 1L;

    public BeanXmlException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanXmlException(Throwable cause) {
        super(cause);
    }

    public BeanXmlException(String message) {
        super(message);
    }
}
