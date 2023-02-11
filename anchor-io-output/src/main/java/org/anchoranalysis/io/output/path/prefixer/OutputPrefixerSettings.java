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

    /** The Unix separator character. */
    private static final char UNIX_SEPARATOR = '/';

    /** The Windows separator character. */
    private static final char WINDOWS_SEPARATOR = '\\';

    /** The character to replace directory separators with when suppressed. */
    private static final char REPLACEMENT_CHARACTER = '_';

    // START: GETTERS AND SETTERS
    /** A directory indicating where inputs can be located */
    @Getter private Optional<Path> outputDirectory = Optional.empty();

    /** A file format suggested for writing images to the file system. */
    @Getter private Optional<ImageFileFormat> suggestedImageOutputFormat = Optional.empty();

    /**
     * When true, requests outputting with an incrementing number sequence, rather than the usual
     * outputter (normally based upon input filenames).
     */
    @Getter private boolean outputIncrementingNumberSequence = false;

    /**
     * When true, Requests that the experiment-identifier (name and index) is not included in the
     * output-directory path.
     */
    @Getter private boolean omitExperimentIdentifier = false;
    // END: GETTERS AND SETTERS

    /**
     * When true, Requests suppressing directories (replacing subdirectory separators with an
     * underscore) in the identifiers that are outputted.
     */
    private boolean outputSuppressDirectories = false;

    /**
     * Derives an identifier that maybe has its directories suppressed.
     *
     * <p>This can leave {@code identifier} unchanged, or suppress the subdirectories in identifier
     * by replacing them with underscores.
     *
     * @param identifier the identifier whose directories are maybe suppressed.
     * @param forceSuppressDirectories if true, forces the suppression of directories, regardless of
     *     {@code outputSuppressDirectories}
     * @return the identifier after any suppression of directories is maybe applied.
     */
    public String maybeSuppressDirectories(String identifier, boolean forceSuppressDirectories) {
        if (forceSuppressDirectories || outputSuppressDirectories) {
            return replaceSeperatorsWithUnderscore(identifier);
        } else {
            return identifier;
        }
    }

    public void assignOutputDirectory(Path outputDirectory) {
        this.outputDirectory = Optional.of(outputDirectory);
    }

    /**
     * Assigns a suggestion for a preferred image-output format.
     *
     * @param format the preferred format.
     */
    public void assignSuggestedImageOutputFormat(ImageFileFormat format) {
        this.suggestedImageOutputFormat = Optional.of(format);
    }

    /**
     * Requests outputting with an incrementing number sequence, rather than the usual outputter
     * (normally based upon input filenames).
     */
    public void requestOutputIncrementingNumberSequence() {
        this.outputIncrementingNumberSequence = true;
    }

    /**
     * Requests suppressing directories (replacing subdirectory separators with an underscore) in
     * the identifiers that are outputted.
     */
    public void requestOutputSuppressDirectories() {
        this.outputSuppressDirectories = true;
    }

    /**
     * Requests that the experiment-identifier (name and index) is not included in the
     * output-directory path.
     */
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

    private static String replaceSeperatorsWithUnderscore(String string) {
        return string.replace(WINDOWS_SEPARATOR, REPLACEMENT_CHARACTER)
                .replace(UNIX_SEPARATOR, REPLACEMENT_CHARACTER);
    }
}
