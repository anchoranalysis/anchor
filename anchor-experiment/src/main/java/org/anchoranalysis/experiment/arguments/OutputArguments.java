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

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.recorded.OutputEnabledDelta;

/**
 * Arguments that can further specify an experiment's <b>output</b> in addition to its bean
 * specification.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class OutputArguments {

    /** A directory indicating where inputs can be located */
    @Getter private Optional<Path> outputDirectory = Optional.empty();

    /**
     * Additions/subtractions of outputs for the experiment supplied by the user.
     *
     * <p>These are applied to an existing source of output-enabled rules (e.g. defaults from a
     * task, or rules defined in the experiment's {@link OutputManager}.
     */
    @Getter private OutputEnabledDelta outputEnabledDelta = new OutputEnabledDelta();

    /** A file format suggested for writing images to the file system. */
    @Getter private Optional<ImageFileFormat> suggestedImageOutputFormat = Optional.empty();

    /**
     * Requests outputting with an incrementing number sequence, rather than the usual outputter
     * (normally based upon input filenames).
     */
    @Getter private boolean outputIncrementingNumberSequence = false;

    public void assignOutputDirectory(Path outputDirectory) {
        this.outputDirectory = Optional.of(outputDirectory);
    }

    public void assignSuggestedImageOutputFormat(ImageFileFormat format) {
        this.suggestedImageOutputFormat = Optional.of(format);
    }

    public void requestOutputIncrementingNumberSequence() {
        this.outputIncrementingNumberSequence = true;
    }
}