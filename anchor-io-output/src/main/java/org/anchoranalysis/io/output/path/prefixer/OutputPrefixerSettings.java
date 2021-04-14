/*-
 * #%L
 * anchor-io-output
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.output.path.prefixer;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.format.ImageFileFormat;

/**
 * Arguments influencing into which directory outputs are written, and how identifiers are
 * expressed.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class OutputPrefixerSettings {

    /** A directory indicating where inputs can be located */
    @Getter private Optional<Path> outputDirectory = Optional.empty();

    /** A file format suggested for writing images to the file system. */
    @Getter private Optional<ImageFileFormat> suggestedImageOutputFormat = Optional.empty();

    /**
     * Requests outputting with an incrementing number sequence, rather than the usual outputter
     * (normally based upon input filenames).
     */
    @Getter private boolean outputIncrementingNumberSequence = false;

    /**
     * Requests suppressing directories (replacing subdirectory separators with an underscore) in
     * the identifiers that are outputted.
     */
    @Getter private boolean outputSuppressDirectories = false;

    /**
     * Requests that the experiment-identifier (name and index) is not included in the
     * output-directory path.
     */
    @Getter private boolean omitExperimentIdentifier = false;

    public void assignOutputDirectory(Path outputDirectory) {
        this.outputDirectory = Optional.of(outputDirectory);
    }

    public void assignSuggestedImageOutputFormat(ImageFileFormat format) {
        this.suggestedImageOutputFormat = Optional.of(format);
    }

    public void requestOutputIncrementingNumberSequence() {
        this.outputIncrementingNumberSequence = true;
    }

    public void requestOutputSuppressDirectories() {
        this.outputSuppressDirectories = true;
    }

    public void requestOmitExperimentIdentifier() {
        this.omitExperimentIdentifier = true;
    }

    public void checkAbsolutePath() throws PathPrefixerException {
        if (outputDirectory.isPresent() && !outputDirectory.get().isAbsolute()) {
            throw new PathPrefixerException(
                    String.format(
                            "An non-absolute path was passed to %s of %s",
                            this.getClass().getSimpleName(), outputDirectory.get()));
        }
    }
}
