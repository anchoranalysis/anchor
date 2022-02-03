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
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.io.input.InputContextParameters;

/**
 * Arguments that can further specify an experiment's <b>input</b> in addition to its bean
 * specification.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class InputArguments {

    /** Context parameters that an influence determining inputs. */
    @Getter private InputContextParameters contextParameters = new InputContextParameters();

    /**
     * If True, any files in the input directory that are unused as inputs, are copied to the output
     * directory.
     */
    @Getter private boolean copyNonInputs = false;

    /** A directory indicating where models can be located */
    private Optional<Path> modelDirectory = Optional.empty();

    public Path getModelDirectory() {
        return modelDirectory.orElseThrow(
                () -> new AnchorFriendlyRuntimeException("Model-directory is required but absent"));
    }

    public void assignModelDirectory(Path modelDirectory) {
        this.modelDirectory = Optional.of(modelDirectory);
    }

    public void assignCopyNonInputs() {
        this.copyNonInputs = true;
    }
}
