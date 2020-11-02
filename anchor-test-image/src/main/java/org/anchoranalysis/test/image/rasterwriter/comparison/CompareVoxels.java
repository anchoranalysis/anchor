package org.anchoranalysis.test.image.rasterwriter.comparison;

import java.io.FileNotFoundException;
import org.anchoranalysis.test.image.DualComparer;
import org.anchoranalysis.test.image.rasterwriter.ExtensionAdder;

/**
 * Compares two image-files, by reading both images, and checking that the dimensions and voxel intensities exactly match.
 * 
 * @author Owen Feehan
 *
 */
class CompareVoxels extends CompareBase {

    private String extensionToCompareTo;
    
    public CompareVoxels(DualComparer comparer, String extensionToCompareTo) {
        super(comparer);
        this.extensionToCompareTo = extensionToCompareTo;
    }

    @Override
    protected boolean areIdentical(String filenameWithoutExtension, String filenameWithExtension)
            throws FileNotFoundException {
        return comparer
                .compareTwoImages(
                        filenameWithExtension,
                        ExtensionAdder.addExtension(
                                filenameWithoutExtension,
                                extensionToCompareTo),
                        true);
    }

    @Override
    protected String identifierForTest() {
        return "_voxelwiseCompare";
    }
}
