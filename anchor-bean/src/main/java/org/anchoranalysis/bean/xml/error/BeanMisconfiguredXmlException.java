/* (C)2020 */
package org.anchoranalysis.bean.xml.error;

import org.anchoranalysis.core.error.combinable.AnchorCombinableException;
import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * A bean's XML is thrown when a bean has misconfigured XML
 *
 * <p>As these exceptions tend to get nested inside each other, we need to eventually combine them,
 * so that only the final-most errored bean is displayed to the user
 *
 * @author Owen Feehan
 */
public class BeanMisconfiguredXmlException extends AnchorCombinableException {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     *
     * @param msg the message we want to display to the user about the exception
     * @param cause what caused it
     */
    public BeanMisconfiguredXmlException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructor
     *
     * @param cause what caused it
     */
    public BeanMisconfiguredXmlException(Throwable cause) {
        super("", cause);
    }

    @Override
    protected boolean canExceptionBeCombined(Throwable exc) {
        return exc instanceof BeanMisconfiguredXmlException;
    }

    /**
     * This summarize() option just looks for the most deep exception that can be 'combined' and
     * takes its message
     *
     * @return an exception that summarizes this exception (and maybe some nested-exceptions)
     */
    @Override
    public Throwable summarize() {
        return super.findMostDeepCombinableException();
    }

    @Override
    protected boolean canExceptionBeSkipped(Throwable exc) {
        return exc instanceof ConfigurationRuntimeException || exc instanceof BeanXmlException;
    }

    @Override
    protected String createMessageForDscr(String dscr) {
        return dscr;
    }
}
