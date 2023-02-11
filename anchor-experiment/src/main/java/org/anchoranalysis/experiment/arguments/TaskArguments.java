/*-
 * #%L
 * anchor-experiment
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
package org.anchoranalysis.experiment.arguments;

import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.index.range.IndexRangeNegative;
import org.anchoranalysis.core.index.range.IndexRangeNegativeFactory;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.image.core.dimensions.size.suggestion.ImageSizeSuggestion;
import org.anchoranalysis.image.core.dimensions.size.suggestion.ImageSizeSuggestionFactory;
import org.anchoranalysis.image.core.dimensions.size.suggestion.SuggestionFormatException;

/**
 * Arguments that can further specify an experiment's <b>task</b> in addition to its bean
 * specification.
 *
 * @author Owen Feehan
 */
public class TaskArguments {

    /** A name to describe the ongoing task. */
    @Getter private Optional<String> taskName = Optional.empty();

    /** Suggests dimensions or a scaling-factor to resize an image to. */
    @Getter private Optional<ImageSizeSuggestion> size;

    /** Suggests a maximum number of processors (CPUs) for a task. */
    @Getter private Optional<Integer> maxNumberProcessors = Optional.empty();

    /**
     * An index-range to use for grouping, by subsetting components from each input's identifier.
     */
    @Getter private Optional<IndexRangeNegative> groupIndexRange = Optional.empty();

    /** Creates with no initial size. */
    public TaskArguments() {
        this.size = Optional.empty();
    }

    /**
     * Creates with a specific initial size-suggestion.
     *
     * @param size the size-suggestion.
     */
    public TaskArguments(Optional<ImageSizeSuggestion> size) {
        this.size = size;
    }

    /**
     * Assigns a name for the task.
     *
     * @param taskName the name to assign.
     */
    public void assignTaskName(Optional<String> taskName) {
        this.taskName = taskName;
    }

    /**
     * Assign dimensions or scaling factor or size for an image, as may be used by a task.
     *
     * @param size a string describing the size in a format compatible with {@link
     *     ImageSizeSuggestionFactory#create(String)}.
     * @throws ExperimentExecutionException if the format of size is invalid.
     */
    public void assignSize(String size) throws ExperimentExecutionException {
        try {
            this.size = Optional.of(ImageSizeSuggestionFactory.create(size));
        } catch (SuggestionFormatException e) {
            throw new ExperimentExecutionException(e);
        }
    }

    /**
     * Assigns an index-range to use to form groups, by subsetting components from each input's
     * identifier.
     *
     * @param groupRange a string in the format expected by {@link IndexRangeNegativeFactory#parse},
     *     or an empty-string which is considered as "0".
     * @throws ExperimentExecutionException if {@code groupRange} does have the expected format.
     */
    public void assignGroup(String groupRange) throws ExperimentExecutionException {
        try {
            if (groupRange.isEmpty()) {
                // Default to 0, if no string is specified
                groupRange = "0";
            }
            this.groupIndexRange = Optional.of(IndexRangeNegativeFactory.parse(groupRange));
        } catch (OperationFailedException e) {
            throw new ExperimentExecutionException(e);
        }
    }

    /**
     * Assigns a suggested maximum number of processors (CPUs) for a task.
     *
     * @param numberProcessors the maximum number of processors.
     * @throws ExperimentExecutionException if the string doesn't describe a positive integer.
     */
    public void assignMaxNumberProcessors(String numberProcessors)
            throws ExperimentExecutionException {
        try {
            Integer value = Integer.valueOf(numberProcessors);

            if (value <= 0) {
                throw positiveNumberProcessorsException(numberProcessors);
            }

            this.maxNumberProcessors = Optional.of(value);
        } catch (NumberFormatException e) {
            throw positiveNumberProcessorsException(numberProcessors);
        }
    }

    private static ExperimentExecutionException positiveNumberProcessorsException(
            String numberProcessors) {
        return new ExperimentExecutionException(
                String.format(
                        "The number of processors must be a positive integer. %s is invalid.",
                        numberProcessors));
    }
}
