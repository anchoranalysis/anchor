package org.anchoranalysis.image.object.morphological;

import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.ConditionalKernel;
import org.anchoranalysis.image.voxel.kernel.dilateerode.DilationKernel3;
import org.anchoranalysis.image.voxel.kernel.dilateerode.DilationKernel3ZOnly;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DilationKernelFactory {

    /** selects which dimensions dilation is applied on. */
    private final SelectDimensions dimensions;
    
    /** If true, pixels outside the buffer are treated as ON, otherwise as OFF. */
    private final boolean outsideAtThreshold;
    
    private final boolean bigNeighborhood;

    public BinaryKernel createDilationKernel(
            BinaryValuesByte binaryValues,
            Optional<Voxels<UnsignedByteBuffer>> background,
            int minIntensityValue)
            throws CreateException {

        BinaryKernel kernelDilation = createDilationKernel(binaryValues);

        if (minIntensityValue > 0 && background.isPresent()) {
            return new ConditionalKernel(kernelDilation, minIntensityValue, background.get());
        } else {
            return kernelDilation;
        }
    }
    
    private BinaryKernel createDilationKernel(BinaryValuesByte binaryValues)
            throws CreateException {
        if (dimensions == SelectDimensions.Z_ONLY) {
            if (bigNeighborhood) {
                throw new CreateException("Big-neighborhood not supported for zOnly");
            }

            return new DilationKernel3ZOnly(binaryValues, outsideAtThreshold);
        } else {
            return new DilationKernel3(
                    binaryValues,
                    outsideAtThreshold,
                    dimensions == SelectDimensions.ALL_DIMENSIONS,
                    bigNeighborhood);
        }
    }
}
