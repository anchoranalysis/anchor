package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.io.bioformats.copyconvert.ImageFileEncoding;

/**
 * Like {@link ToUnsignedByte} but applies scaling, if necessary, to map the original value to
 * 8-bits.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public abstract class ToUnsignedByteWithScaling extends ToUnsignedByte {

    // START REQUIRED ARGUMENTS
    /**
     * The number of bits that are used in the input-type.
     *
     * <p>e.g. 8 or 12 or 16.
     *
     * <p>This should always be a positive number.
     *
     * <p>If {@code > 8}, then scaling is applied to values, to map them to the 8-bits available in
     * an <i>unsigned-byte</i>.
     *
     * <p>If {@code <= 8}, then no scaling is applied.
     */
    private final int effectiveBits;
    // END REQUIRED ARGUMENTS

    private Optional<ApplyScaling> applyScaling;

    @Override
    protected void setupBefore(Dimensions dimensions, ImageFileEncoding encoding)
            throws IOException {
        super.setupBefore(dimensions, encoding);
        applyScaling = calculateConvertRatio();
    }

    /**
     * Scales a value, if necessary, to map it to 8-bits.
     *
     * @param unscaled the unscaled value, which may use more than 8-bits.
     * @return the scaledValue, should now fit inside 8-bits.
     */
    protected int scaleValue(int unscaled) {
        if (applyScaling.isPresent()) {
            return applyScaling.get().scale(unscaled);
        } else {
            return unscaled;
        }
    }

    /** How much to scale each value by. */
    private Optional<ApplyScaling> calculateConvertRatio() {
        if (effectiveBits <= 8) {
            return Optional.empty();
        } else {
            float conversionRatio = ConvertHelper.twoToPower(8 - effectiveBits);
            return Optional.of(new ApplyScaling(conversionRatio));
        }
    }
}
