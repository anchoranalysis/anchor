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
