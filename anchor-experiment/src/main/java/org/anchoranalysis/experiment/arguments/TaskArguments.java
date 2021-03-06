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
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class TaskArguments {

    /** A name to describe the ongoing task. */
    @Getter private Optional<String> taskName = Optional.empty();

    /** Suggests dimensions or a scaling-factor to resize an image to. */
    @Getter private Optional<ImageSizeSuggestion> size = Optional.empty();

    /** Suggests a maximum number of processors (CPUs) for a task. */
    @Getter private Optional<Integer> maxNumberProcessors = Optional.empty();

    public void assignTaskName(Optional<String> taskName) {
        this.taskName = taskName;
    }

    public void assignSize(String size) throws ExperimentExecutionException {
        try {
            this.size = Optional.of(ImageSizeSuggestionFactory.create(size));
        } catch (SuggestionFormatException e) {
            throw new ExperimentExecutionException(e);
        }
    }

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
