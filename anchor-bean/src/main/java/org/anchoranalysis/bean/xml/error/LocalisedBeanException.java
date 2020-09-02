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
 * <p>All nested exceptions are traversed until null is found as a cause.
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
