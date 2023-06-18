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
package org.anchoranalysis.io.output.writer;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Outputs an element to the file-system.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ElementOutputter {

    /** The outputter for writing the element. */
    @Getter private OutputterChecked outputter;

    /** Records the execution time of particular operations. */
    @Getter private ExecutionTimeRecorder executionTimeRecorder;

    /** Supplies a logger for information messages when outputting. */
    private Supplier<Optional<Logger>> logger;

    /**
     * Derives a bound-output-manager for a (possibly newly created) subdirectory of the existing
     * manager.
     *
     * @param subdirectoryName the subdirectory-name.
     * @param inheritOutputRulesAndRecording if true, the output rules and recording are inherited
     *     from the parent directory. if false, they are not, and all outputs are allowed and are
     *     unrecorded.
     * @return a bound-output-manager for the subdirectory.
     */
    public OutputterChecked deriveSubdirectory(
            String subdirectoryName, boolean inheritOutputRulesAndRecording) {
        return outputter.deriveSubdirectory(subdirectoryName, inheritOutputRulesAndRecording);
    }

    /**
     * Creates a full absolute path that completes the part of the path present in the outputter
     * with an additional suffix.
     *
     * @param suffixWithoutExtension the suffix for the path (without any extension).
     * @param extension the file extension (without a leading period).
     * @param fallbackSuffix if neither a {@code prefix} is defined nor a {@code suffix}, then this
     *     provides a suffix to use so a file isn't only an extension.
     * @return a newly created absolute path, combining directory, prefix (if it exists), suffix and
     *     extension.
     */
    public Path makeOutputPath(
            Optional<String> suffixWithoutExtension, String extension, String fallbackSuffix) {
        Path path = outputter.makeOutputPath(suffixWithoutExtension, extension, fallbackSuffix);
        makeAnyNecessarySubdirectories(path);
        return path;
    }

    /**
     * General settings for outputting.
     *
     * @return the settings.
     */
    public OutputWriteSettings getSettings() {
        return outputter.getSettings();
    }

    /**
     * A suggestion on what file-format to write.
     *
     * @return a suggestion for the file-format, if known.
     */
    public Optional<ImageFileFormat> getSuggestedFormatToWrite() {
        return outputter.getContext().getSuggestedFormatToWrite();
    }

    /**
     * Which outputs are enabled or not enabled.
     *
     * @return an interface that can be used to determine if an individual output is enabled.
     */
    public MultiLevelOutputEnabled getOutputsEnabled() {
        return outputter.getOutputsEnabled();
    }

    /**
     * The associated logger.
     *
     * @return the logger.
     */
    public Optional<Logger> logger() {
        return logger.get();
    }

    /** Create any necessary subdirectories so that {@code path} can be created as a file. */
    private static void makeAnyNecessarySubdirectories(Path path) {
        if (path.getParent() != null) {
            path.getParent().toFile().mkdirs();
        }
    }
}
