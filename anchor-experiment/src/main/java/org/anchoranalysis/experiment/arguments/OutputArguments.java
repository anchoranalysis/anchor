package org.anchoranalysis.experiment.arguments;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.recorded.OutputEnabledDelta;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Arguments that can further specify an experiment's <b>output</b> in addition to its bean specification.
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

    /**
     * A file format suggested for writing images to the file system.
     */
    @Getter private Optional<ImageFileFormat> suggestedImageOutputFormat;

    public void assignOutputDirectory(Path outputDirectory) {
        this.outputDirectory = Optional.of(outputDirectory);
    }
    
    public void assignSuggestedImageOutputFormat(ImageFileFormat format) {
        this.suggestedImageOutputFormat = Optional.of(format);
    }
}
