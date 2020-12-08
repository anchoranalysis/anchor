package org.anchoranalysis.experiment.arguments;

import java.util.Optional;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.image.core.dimensions.resize.suggestion.ImageResizeSuggestion;
import org.anchoranalysis.image.core.dimensions.resize.suggestion.ImageResizeSuggestionFactory;
import org.anchoranalysis.image.core.dimensions.resize.suggestion.SuggestionFormatException;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Arguments that can further specify an experiment's <b>task</b> in addition to its bean
 * specification.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class TaskArguments {

    /** A name to describe the ongoing task */
    @Getter private Optional<String> taskName = Optional.empty();
    
    /** Suggests dimensions or a scaling-factor to resize an image to. */
    @Getter private Optional<ImageResizeSuggestion> resize = Optional.empty(); 
    
    public void assignTaskName(Optional<String> taskName) {
        this.taskName = taskName;
    }
    
    public void assignResize(String resize) throws ExperimentExecutionException {
        try {
            this.resize = Optional.of( ImageResizeSuggestionFactory.create(resize) );
        } catch (SuggestionFormatException e) {
            throw new ExperimentExecutionException(e);
        }
    }
}
