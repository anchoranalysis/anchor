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
