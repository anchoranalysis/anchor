package org.anchoranalysis.image.object;

import lombok.Value;

@Value
public class OverlappingObject {

    /** Original unscaled object before any operations */
    private ObjectMask original;

    /** After pre-operation but before scaling */
    private ObjectMask afterPreoperation;
}
