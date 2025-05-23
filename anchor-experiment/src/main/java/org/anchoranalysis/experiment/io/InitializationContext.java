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
package org.anchoranalysis.experiment.io;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.dimensions.size.suggestion.ImageSizeSuggestion;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Context for creating initialization-parameters.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class InitializationContext {

    /** The input-output context. */
    @Getter private final InputOutputContext inputOutput;

    /** A suggested input on how to resize an image, if one is provided. */
    @Getter private final Optional<ImageSizeSuggestion> suggestedSize;

    /**
     * Create with an {@link InputOutputContext}.
     *
     * @param inputOutputContext the input-output context.
     */
    public InitializationContext(InputOutputContext inputOutputContext) {
        this(inputOutputContext, Optional.empty());
    }

    /**
     * An outputter that writes to the particular output-directory.
     *
     * @return the outputter.
     */
    public Outputter getOutputter() {
        return inputOutput.getOutputter();
    }

    /**
     * Creates a {@link CommonContext} a context that contains a subset of this context.
     *
     * @return a newly created {@link CommonContext} reusing the objects from this context.
     */
    public CommonContext common() {
        return inputOutput.common();
    }

    /**
     * The associated logger.
     *
     * @return the logger associated with input-output operations.
     */
    public Logger getLogger() {
        return inputOutput.getLogger();
    }
}
