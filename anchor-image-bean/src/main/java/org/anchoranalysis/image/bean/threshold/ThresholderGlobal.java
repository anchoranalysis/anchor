/* (C)2020 */
package org.anchoranalysis.image.bean.threshold;

import java.nio.ByteBuffer;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.thresholder.VoxelBoxThresholder;

public class ThresholderGlobal extends Thresholder {

    // START BEAN PARAMETERS
    @BeanField @Getter @Setter private CalculateLevel calculateLevel;
    // END BEAN PARAMETERS

    @Override
    public BinaryVoxelBox<ByteBuffer> threshold(
            VoxelBoxWrapper inputBuffer,
            BinaryValuesByte bvOut,
            Optional<Histogram> histogram,
            Optional<ObjectMask> mask)
            throws OperationFailedException {
        return thresholdForHistogram(
                histogramBuffer(inputBuffer, histogram, mask), inputBuffer, bvOut, mask);
    }

    private BinaryVoxelBox<ByteBuffer> thresholdForHistogram(
            Histogram hist,
            VoxelBoxWrapper inputBuffer,
            BinaryValuesByte bvOut,
            Optional<ObjectMask> mask)
            throws OperationFailedException {

        int thresholdVal = calculateLevel.calculateLevel(hist);
        assert (thresholdVal >= 0);
        return VoxelBoxThresholder.thresholdForLevel(inputBuffer, thresholdVal, bvOut, mask, false);
    }

    private Histogram histogramBuffer(
            VoxelBoxWrapper inputBuffer, Optional<Histogram> histogram, Optional<ObjectMask> mask) {
        return histogram.orElseGet(() -> HistogramFactory.create(inputBuffer, mask));
    }
}
