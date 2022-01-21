package org.anchoranalysis.image.voxel.iterator;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * The minimum and maximum associated with a range of values.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class MinMaxRange {
    /** The minimum value. */
    private final long min;

    /** The maximum value. */
    private final long max;
}
