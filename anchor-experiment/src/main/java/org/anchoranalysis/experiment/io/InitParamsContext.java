package org.anchoranalysis.experiment.io;

import java.util.Optional;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.dimensions.resize.suggestion.ImageResizeSuggestion;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.Outputter;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Context for creating initialization-params.
 * 
 * @author Owen Feehan
 */
@AllArgsConstructor
public class InitParamsContext {

    /** The input-output context. */
    @Getter private final InputOutputContext inputOutput;
    
    /** A suggested input on how to resize an image, if one is provided. */
    @Getter private final Optional<ImageResizeSuggestion> suggestedResize;

    public InitParamsContext(InputOutputContext inputOutput) {
        this(inputOutput, Optional.empty());
    }
    
    public Outputter getOutputter() {
        return inputOutput.getOutputter();
    }

    public CommonContext common() {
        return inputOutput.common();
    }

    public Logger getLogger() {
        return inputOutput.getLogger();
    }
}
