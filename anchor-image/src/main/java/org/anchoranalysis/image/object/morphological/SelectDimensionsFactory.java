package org.anchoranalysis.image.object.morphological;

import org.anchoranalysis.core.error.OperationFailedException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class SelectDimensionsFactory {
    
    public static SelectDimensions of(boolean do3D) {
        return do3D ? SelectDimensions.ALL_DIMENSIONS : SelectDimensions.X_Y_ONLY;
    }
    
    public static SelectDimensions of(boolean do3D, boolean zOnly) throws OperationFailedException {
        if (zOnly) {
            if (do3D) {
                return SelectDimensions.Z_ONLY;
            } else {
                throw new OperationFailedException("If zOnly is true, then do3D must also be true");
            }
        } else {
            return of (do3D);
        }
    }
}
