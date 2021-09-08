package org.anchoranalysis.core.exception.combinable;

/*
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import java.util.StringJoiner;
import lombok.Getter;
import org.anchoranalysis.core.exception.AnchorCheckedException;

/**
 * An exception that can be combined with similar exceptions and summarized.
 *
 * <p>A special type of exception where lots of nested exceptions of the same type can be combined
 * together to create a new exception summarizing them all together.
 *
 * <p>This is useful for nested-exceptions which could be better expressed as a path e.g. {@code
 * /cause3/cause2/cause1/exception}
 *
 * <p>It is also supported to skip certain exception types when traversing through the hierarchy of
 * nested exceptions (i.e. {@code getCause().getCause().getCause()}.
 *
 * <p>When skipped, the exception is ignored, and its getCause() is then processed.
 *
 * <p>Otherwise, if a {@code getCause()} is encountered that can be neither combined, nor skipped,
 * the process ends.
 *
 * <p>Finally, a {@link org.anchoranalysis.core.exception.combinable.SummaryException} is created
 * representing the combined beans. Its getCause() is set as the getCause() of the last bean to be
 * combined.
 *
 * @author Owen Feehan
 */
public abstract class AnchorCombinableException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = -6966810405755062033L;

    /** The (uncombined) description associated with the exception. */
    @Getter private final String description;

    /**
     * Creates with a description and a cause.
     *
     * @param description the description.
     * @param cause the cause.
     */
    protected AnchorCombinableException(String description, Throwable cause) {
        super(
                description,
                cause); // This message should never be seen, as we should always call the summarize
        // functionality
        this.description = description;
    }

    /**
     * Can another exception be combined with this exception?
     *
     * @param exception the other exception to check if it can be combined.
     * @return true iff this exception is compatible to be combined with the current exception.
     */
    protected abstract boolean canExceptionBeCombined(Throwable exception);

    /**
     * Can another exception be skipped, when we combine with the current exception?
     *
     * @param exception the other exception to check if it can be skipped.
     * @return true iff this exception is compatible to be skipped, when processing the current
     *     exception.
     */
    protected abstract boolean canExceptionBeSkipped(Throwable exception);

    /**
     * Creates a new {link Throwable} that summarizes this exception and any nested causes of the
     * exception.
     *
     * <p>This can effectively combine a chain of nested exceptions into a single exception without
     * a cause.
     *
     * @return an exception summarizing the current exception, and any causes.
     */
    public abstract Throwable summarize();

    /**
     * Creates a message for the exception from the description.
     *
     * @param description either a single description, or a combined description
     * @return a message describing an error, incorporating description
     */
    protected abstract String createMessageForDescription(String description);

    /**
     * Traverses through a set of nested-exceptions creating a description for each "combined"
     * exception, and combining them into a single string.
     *
     * @param prefix a string to be inserted initially
     * @param seperator a separator is placed between the description of each exception
     * @return a string as above
     */
    protected Throwable combineDescriptionsRecursively(String prefix, String seperator) {

        StringJoiner joiner = new StringJoiner(seperator, prefix, "");

        Throwable finalClause = this;
        Throwable exception = this;
        do {
            if (canExceptionBeCombined(exception)) {

                joinException(exception, joiner);

                finalClause = exception.getCause();
                exception = exception.getCause();

            } else if (canExceptionBeSkipped(exception)) {
                exception = exception.getCause();
            } else {
                // If it's on neither or Combine-List or Skip-List we get out
                break;
            }
        } while (exception != null);

        return createCombinedException(joiner.toString(), finalClause);
    }

    /**
     * Traverses through the nested-exceptions and finds the most deep exception that is combinable.
     *
     * <p>This determination uses the rules about combining and skipping outlined in the class
     * description.
     *
     * @return the depth-most exception that is found by this search
     */
    protected Throwable findMostDeepCombinableException() {

        Throwable finalException = this;
        Throwable exception = this;
        do {
            if (canExceptionBeCombined(exception)) {
                finalException = exception;
                exception = exception.getCause();
            } else if (canExceptionBeSkipped(exception)) {
                // We skip certain types of exceptions (basically ignore them, and move to the next
                // cause)
                exception = exception.getCause();
            } else {
                // If it's on neither or Combine-List or Skip-List we get out
                break;
            }
        } while (exception != null);

        return finalException;
    }

    /**
     * Are there nested-exceptions (according to our traversal rules) that are combinable?
     *
     * @return true if there are, false otherwise.
     */
    protected boolean hasNoCombinableNestedExceptions() {
        return findMostDeepCombinableException() == this;
    }

    private void joinException(Throwable exception, StringJoiner joiner) {
        AnchorCombinableException exceptionCast = (AnchorCombinableException) exception;
        joiner.add(exceptionCast.getDescription());
    }

    private Throwable createCombinedException(String combinedNames, Throwable finalClause) {
        return new SummaryException(createMessageForDescription(combinedNames), finalClause);
    }
}
