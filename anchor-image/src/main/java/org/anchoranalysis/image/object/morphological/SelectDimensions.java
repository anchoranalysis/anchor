package org.anchoranalysis.image.object.morphological;

public enum SelectDimensions {
    /** The dilation applies only in the Z-dimension. */
    Z_ONLY,

    /** The dilation applies in X and Y -dimensions only, and not in Z-dimension. */
    X_Y_ONLY,

    /** The dilation applies in all dimensions (X, Y and Z) */
    ALL_DIMENSIONS
}
