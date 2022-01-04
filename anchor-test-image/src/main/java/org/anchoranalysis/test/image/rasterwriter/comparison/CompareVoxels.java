/*-
 * #%L
 * anchor-test-image
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
package org.anchoranalysis.test.image.rasterwriter.comparison;

import java.io.FileNotFoundException;
import org.anchoranalysis.test.image.DualComparer;
import org.anchoranalysis.test.image.stackwriter.ExtensionAdder;

/**
 * Compares two image-files, by reading both images, and checking that the dimensions and voxel
 * intensities exactly match.
 *
 * @author Owen Feehan
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
        return comparer.compareTwoImages(
                filenameWithExtension,
                ExtensionAdder.addExtension(filenameWithoutExtension, extensionToCompareTo),
                true);
    }

    @Override
    protected String identifierForTest() {
        return "_voxelwiseCompare";
    }
}
