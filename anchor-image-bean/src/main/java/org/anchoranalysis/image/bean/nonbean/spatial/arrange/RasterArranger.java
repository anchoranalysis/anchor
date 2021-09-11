/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.nonbean.spatial.arrange;

import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

public class RasterArranger {

    private BoundingBoxesOnPlane boundingBoxes;
    private Dimensions dimensions;

    public void initialize(ArrangeStack arrange, List<RGBStack> list) throws InitializeException {

        Iterator<RGBStack> rasterIterator = list.iterator();
        try {
            this.boundingBoxes = arrange.createBoundingBoxesOnPlane(rasterIterator);
        } catch (ArrangeStackException e) {
            throw new InitializeException(e);
        }

        if (rasterIterator.hasNext()) {
            throw new InitializeException("rasterIterator has more items than can be accomodated");
        }

        dimensions = new Dimensions(boundingBoxes.extent());
    }

    public RGBStack createStack(List<RGBStack> list, ChannelFactorySingleType factory) {
        RGBStack stackOut = new RGBStack(dimensions, factory);
        ppltStack(list, stackOut.asStack());
        return stackOut;
    }

    private void ppltStack(List<RGBStack> generatedImages, Stack out) {

        int index = 0;
        for (RGBStack image : generatedImages) {

            BoundingBox box = this.boundingBoxes.get(index++);

            // NOTE
            // For a special case where our projection z-extent is different to our actual z-extent,
            // that means
            //   we should repeat
            if (box.extent().z() != image.dimensions().z()) {

                int zShift = 0;
                do {
                    projectImageOntoStackOut(box, image.asStack(), out, zShift);
                    zShift += image.dimensions().z();
                } while (zShift < out.dimensions().z());

            } else {
                projectImageOntoStackOut(box, image.asStack(), out, 0);
            }
        }
    }

    private void projectImageOntoStackOut(
            BoundingBox box, Stack stackIn, Stack stackOut, int zShift) {

        assert (stackIn.getNumberChannels() == stackOut.getNumberChannels());

        Extent extent = stackIn.extent();
        Extent extentOut = stackIn.extent();

        ReadableTuple3i leftCrnr = box.cornerMin();
        int xEnd = leftCrnr.x() + box.extent().x() - 1;
        int yEnd = leftCrnr.y() + box.extent().y() - 1;

        int numC = stackIn.getNumberChannels();
        VoxelBuffer<?>[] voxelsIn = new VoxelBuffer<?>[numC];
        VoxelBuffer<?>[] voxelsOut = new VoxelBuffer<?>[numC];

        for (int z = 0; z < extent.z(); z++) {

            int outZ = leftCrnr.z() + z + zShift;

            if (outZ >= extentOut.z()) {
                return;
            }

            for (int c = 0; c < numC; c++) {
                voxelsIn[c] = stackIn.getChannel(c).voxels().slice(z);
                voxelsOut[c] = stackOut.getChannel(c).voxels().slice(outZ);
            }

            int src = 0;
            for (int y = leftCrnr.y(); y <= yEnd; y++) {
                for (int x = leftCrnr.x(); x <= xEnd; x++) {

                    int outPos = stackOut.dimensions().offset(x, y);

                    for (int c = 0; c < numC; c++) {
                        voxelsOut[c].transferFromConvert(outPos, voxelsIn[c], src);
                    }
                    src++;
                }
            }
        }
    }
}
