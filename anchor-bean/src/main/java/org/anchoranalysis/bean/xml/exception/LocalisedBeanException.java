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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.bean.exception.BeanStrangeException;
import org.anchoranalysis.core.exception.combinable.AnchorCombinableException;

/**
 * Combines a chain of many exceptions into a single exception, searching for an ultimate cause and
 * incorporating the messages from the chain into a single unified message.
 *
 * <p>To do this, all nested exceptions are traversed until null is found as a cause.
 *
 * <p>To determine the message string:
 *
 * <ol>
 *   <li>If the exception is of class {@link LocalisedBeanException}, its {@code message} is added
 *       as a line.
 *   <li>If the exception is another class, it is not included in the message string, but we keep on
 *       traversing.
 * </ol>
 *
 * <p>To determine the cause:
 *
 * <ol>
 *   <li>It finds the deep-most instance of {@link LocalisedBeanException}, and considers its {#link
 *       getCause()} as the cause.
 * </ol>
 *
 * @author Owen Feehan
 */
public class LocalisedBeanException extends AnchorCombinableException {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Create with a message and a cause.
     *
     * @param message the message.
     * @param cause the cause.
     */
    public LocalisedBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    protected boolean canExceptionBeCombined(Throwable exception) {
        return exception instanceof LocalisedBeanException;
    }

    @Override
    public Throwable summarize() {
        return super.combineDescriptionsRecursively("at ", "\n-> ");
    }

    /**
     * If there is no nested-set of combinable exceptions, and the {@code message} related to this
     * current exception is identical as the passed parameter, then we simply ignore the current
     * exception and promote its {@link #getCause()}.
     *
     * <p>e.g. there's no point having two error lines e.g. indicating there's an error at file
     * {@code SOMEPATH}. It's only worthwhile doing this, if there's some extra information, like an
     * include stack.
     *
     * @param pathMatch the path we ignored, if it's localized to the same file (and nowhere else).
     * @return a new exception representing a summary of this exception and its nested causes.
     */
    public BeanXMLException summarizeIgnoreIdenticalFilePath(Path pathMatch) {

        if (hasNoCombinableNestedExceptions()) {

            // let's compare the filepath of the two
            Path pathDescription = Paths.get(getDescription());

            try {
                if (Files.isSameFile(pathMatch, pathDescription)) {
                    // We promote the cause, and remove the current exception from the stack
                    return new BeanXMLException(getCause());
                } else {
                    return new BeanXMLException(summarize());
                }
            } catch (IOException e) {
                // Something goes wrong and we cannot compare the paths
                throw new BeanStrangeException("An IO error has prevented comparisons of paths", e);
            }

        } else {
            // There are exceptions to combine, so we default back the normal summarize operation
            return new BeanXMLException(summarize());
        }
    }

    @Override
    protected boolean canExceptionBeSkipped(Throwable exception) {
        return true;
    }

    @Override
    protected String createMessageForDescription(String message) {
        return message;
    }
}
