/* (C)2020 */
package org.anchoranalysis.bean.xml.error;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.bean.error.BeanStrangeException;
import org.anchoranalysis.core.error.combinable.AnchorCombinableException;

/**
 * Combines many exceptions into a single exception, searching for a Cause and creating message
 * string, incorporating the other strings
 *
 * <p>All nested exceptions are traversed until NULL is found as a cause.
 *
 * <p>To determine the message string: 1. If the exception is of class LocalisedBeanException, its
 * filenameDscr is added as a newline 2. If the exception is another class, it is not included in
 * the message string, but we keep on traversing
 *
 * <p>To determine the Cause 1. It finds the deep-most instance of LocalisedBeanException, and
 * considers its getCause() as the cause
 *
 * @author Owen Feehan
 */
public class LocalisedBeanException extends AnchorCombinableException {

    /** */
    private static final long serialVersionUID = -6966810405755062033L;

    public LocalisedBeanException(String filenameDscr, Throwable cause) {
        super(filenameDscr, cause);
    }

    @Override
    protected boolean canExceptionBeCombined(Throwable exc) {
        return exc instanceof LocalisedBeanException;
    }

    @Override
    public Throwable summarize() {
        return super.combineDscrsRecursively("at ", "\n-> ");
    }

    /**
     * If there is no nested-set of combinable exceptions, and the filenameDscr related to this
     * current exception, is identical as the passed parameter, then we simply ignore the
     * LocalisedBean and promote its getCause()
     *
     * <p>e.g. there's no point having two error lines indicating there's an error at file SOMEPATH
     * it's only worthwhile doing this, if there's some extra information, like an include stack.
     *
     * @param pathMatch the path we ignored, if it's localized to the same file (and nowhere else)
     * @return a new exception representing a summary of this exception and its nested causes
     */
    public BeanXmlException summarizeIgnoreIdenticalFilePath(Path pathMatch) {

        if (hasNoCombinableNestedExceptions()) {

            // let's compare the filepath of the two
            Path pathDesc = Paths.get(getDscr());

            try {
                if (Files.isSameFile(pathMatch, pathDesc)) {
                    // We promote the cause, and remove the current exception from the stack
                    return new BeanXmlException(getCause());
                } else {
                    return new BeanXmlException(summarize());
                }
            } catch (IOException e) {
                // Something goes wrong and we cannot compare the paths
                throw new BeanStrangeException("An IO error has prevented comparisons of paths", e);
            }

        } else {
            // There are exceptions to combine, so we default back the normal summarize operation
            return new BeanXmlException(summarize());
        }
    }

    @Override
    protected boolean canExceptionBeSkipped(Throwable exc) {
        return true;
    }

    @Override
    protected String createMessageForDscr(String dscr) {
        return dscr;
    }
}
