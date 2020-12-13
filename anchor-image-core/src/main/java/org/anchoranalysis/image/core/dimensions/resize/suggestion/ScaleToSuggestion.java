/*-
 * #%L
 * anchor-image-core
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
package org.anchoranalysis.image.core.dimensions.resize.suggestion;

import java.util.Optional;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.resize.ResizeExtentUtilities;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.scale.ScaleFactor;
import org.anchoranalysis.spatial.scale.ScaleFactorUtilities;
import lombok.Value;

/**
 * A series of suggestions to resize images.
 */
@Value
class ScaleToSuggestion implements ImageResizeSuggestion {

    /** The suggested width to resize to. */
    private final Optional<Integer> width;
    
    /** The suggested height to resize to. */
    private final Optional<Integer> height;
    
    /** If true, the aspect ratio must be preserved. */
    private final boolean preserveAspectRatio;

    /**
     * Create with specified parameters.
     * 
     * @param width the suggested width to resize to.
     * @param height the suggested height to resize to.
     * @param preserveAspectRatio if true, the aspect ratio must be preserved.
     * @throws CreateException if neither width nor height is defined
     */
    public ScaleToSuggestion(Optional<Integer> width, Optional<Integer> height,
            boolean preserveAspectRatio) throws CreateException {
        
        if (!width.isPresent() && !height.isPresent()) {
            throw new CreateException("At least one of width and height must be defined");
        }
        
        if ((!width.isPresent() || !height.isPresent()) && !preserveAspectRatio) {
            throw new CreateException("If only one of width or height is specified, then preserveAspectRatio must be true");
        }
        
        this.width = width;
        this.height = height;
        this.preserveAspectRatio = preserveAspectRatio;
    }    
    
    
    @Override
    public ScaleFactor calculateScaleFactor(Optional<Dimensions> dimensionsToBeScaled) throws OperationFailedException {
        if (dimensionsToBeScaled.isPresent()) {
            return calculateForExtent(dimensionsToBeScaled.get().extent());
        } else {
            throw new OperationFailedException("It is necessary to supply existing dimensions for this suggestion, but none are supplied");
        }
    }
    
    private ScaleFactor calculateForExtent(Extent source) {
        if (width.isPresent() && height.isPresent()) {
            Extent target = new Extent(width.get(), height.get());
            return ResizeExtentUtilities.relativeScale(source, target, preserveAspectRatio);
        } else if (width.isPresent()) {
            return createFactorDerived(width.get(), source.x());
        } else if (height.isPresent()) {
            return createFactorDerived(height.get(), source.y());            
        } else {
            throw new AnchorImpossibleSituationException();
        }
    }
    
    private static ScaleFactor createFactorDerived(int target, int source) {
        return new ScaleFactor( ScaleFactorUtilities.deriveScalingFactor(target, source) );  
    }
}
