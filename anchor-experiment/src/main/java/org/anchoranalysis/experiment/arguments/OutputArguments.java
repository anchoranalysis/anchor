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

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.path.prefixer.OutputPrefixerSettings;
import org.anchoranalysis.io.output.recorded.OutputEnabledDelta;

/**
 * Arguments that can further specify an experiment's <b>output</b> in addition to its bean
 * specification.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class OutputArguments {

    /** Where outputs are written, and how identifiers are styled. */
    @Getter private OutputPrefixerSettings prefixer = new OutputPrefixerSettings();

    /**
     * Additions/subtractions of outputs for the experiment supplied by the user.
     *
     * <p>These are applied to an existing source of output-enabled rules (e.g. defaults from a
     * task, or rules defined in the experiment's {@link OutputManager}.
     */
    @Getter private OutputEnabledDelta outputEnabledDelta = new OutputEnabledDelta();

    public void assignSuggestedImageOutputFormat(ImageFileFormat format) {
        prefixer.assignSuggestedImageOutputFormat(format);
    }

    public void requestOutputIncrementingNumberSequence() {
        prefixer.requestOutputIncrementingNumberSequence();
    }

    public void requestOutputSuppressDirectories() {
        prefixer.requestOutputSuppressDirectories();
    }
}
