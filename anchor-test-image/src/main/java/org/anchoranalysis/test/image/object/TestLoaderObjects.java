/*-
 * #%L
 * anchor-test-mpp
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

package org.anchoranalysis.test.image.object;

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.core.mask.Mask;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.binary.connected.ObjectsFromConnectedComponentsFactory;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.test.TestLoader;
import org.anchoranalysis.test.image.io.TestLoaderImage;

/**
 * A utility class for loading and processing object masks in test scenarios.
 *
 * <p>This class provides methods to load image stacks and extract the largest object mask from
 * them. It's primarily used for testing purposes related to image object processing.
 */
@AllArgsConstructor
public class TestLoaderObjects {

    private static final ObjectsFromConnectedComponentsFactory FACTORY =
            new ObjectsFromConnectedComponentsFactory();

    private final TestLoaderImage loader;

    /**
     * Constructs a TestLoaderObjects with a given TestLoader.
     *
     * @param loader The TestLoader to use for loading test data.
     */
    public TestLoaderObjects(TestLoader loader) {
        this.loader = new TestLoaderImage(loader);
    }

    /**
     * Opens the largest object from an image stack identified by a suffix.
     *
     * @param suffix The suffix used to identify the image stack file.
     * @return The largest ObjectMask found in the image stack.
     */
    public ObjectMask openLargestObjectFrom(String suffix) {
        Stack stack = loader.openStackFromTestPath(path(suffix));
        return largestObjectFromStack(stack);
    }

    /** Gets largest connected component from treating a stack as a single-channeled binary-mask. */
    private static ObjectMask largestObjectFromStack(Stack stack) {
        Mask mask = new Mask(stack.getChannel(0));
        return findLargestObject(FACTORY.createUnsignedByte(mask.binaryVoxels()));
    }

    private static ObjectMask findLargestObject(ObjectCollection objects) {
        return objects.streamStandardJava() // NOSONAR
                .max(TestLoaderObjects::compareObjectsByNumberVoxelsOn)
                .get();
    }

    private static int compareObjectsByNumberVoxelsOn(ObjectMask object1, ObjectMask object2) {
        return Integer.compare(object1.numberVoxelsOn(), object2.numberVoxelsOn());
    }

    private static String path(String suffix) {
        return String.format("binaryImageObj/obj%s.tif", suffix);
    }
}
