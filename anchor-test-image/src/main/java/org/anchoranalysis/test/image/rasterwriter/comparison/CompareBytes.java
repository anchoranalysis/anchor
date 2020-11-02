package org.anchoranalysis.test.image.rasterwriter.comparison;

import java.io.IOException;
import org.anchoranalysis.test.image.DualComparer;

/**
 * Compares each byte in two image-files to check if they are all identical.
 * 
 * @author Owen Feehan
 *
 */
class CompareBytes extends CompareBase {

    public CompareBytes(DualComparer comparer) {
        super(comparer);
    }

    @Override
    protected boolean areIdentical(String filenameWithoutExtension, String filenameWithExtension)
            throws IOException {
        return comparer
           .compareTwoBinaryFiles(
                   filenameWithExtension);
    }

    @Override
    protected String identifierForTest() {
        return "_binaryCompare";
    }
}
