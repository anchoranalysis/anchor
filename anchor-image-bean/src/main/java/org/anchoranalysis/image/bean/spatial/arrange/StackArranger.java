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

package org.anchoranalysis.image.bean.spatial.arrange;

import java.util.Iterator;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackArrangement;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Base class for a method that determines positions for {@link RGBStack}s when combined onto a single plane.
 * 
 * @author Owen Feehan
 *
 */
public abstract class StackArranger extends AnchorBean<StackArranger> {

	/**
	 * Creates a new {@link RGBStack} of voxel data type <i>unsigned byte</i> that is a combination of other {@link RGBStack}s.
	 *  
	 * @param stacks the images that will be combined.
	 * @return a newly created {@link RGBStack} with all {@code stacks} copied into it.
	 * @throws ArrangeStackException if there are more {@code stacks} than can be arranged, or otherwise an error occurs arranging them.
	 */
	public RGBStack combine(Iterable<RGBStack> stacks)  throws ArrangeStackException {
		return combine(stacks, ChannelFactory.instance().get(UnsignedByteVoxelType.INSTANCE));
	}
	
	/**
	 * Creates a new {@link RGBStack} that is a combination of other {@link RGBStack}s.
	 *  
	 * @param stacks the images that will be combined.
	 * @param factory the factory used to create the new {@link RGBStack}.
	 * @return a newly created {@link RGBStack} with all {@code stacks} copied into it.
	 * @throws ArrangeStackException if there are more {@code stacks} than can be arranged, or otherwise an error occurs arranging them.
	 */
	public RGBStack combine(Iterable<RGBStack> stacks, ChannelFactorySingleType factory)  throws ArrangeStackException {

        Iterator<RGBStack> rasterIterator = stacks.iterator();
    
    	StackArrangement arrangement = arrangeStacks(rasterIterator);
    	
        if (rasterIterator.hasNext()) {
            throw new ArrangeStackException("There are more stacks that can be arranged.");
        }

        RGBStack stackOut = new RGBStack(arrangement.extent(), factory);
        populateStack(stacks, arrangement, stackOut.asStack());
        return stackOut;
    }
	
	/**
	 * Arranges stacks to that they fit together in a single raster.
	 * 
	 * @param stacks the stacks to arrange.
	 * @return bounding-boxes for each respective {@link RGBStack} in the unified plane.
	 * @throws ArrangeStackException if a bounding-box cannot be determined for any stack.
	 */
    protected abstract StackArrangement arrangeStacks(Iterator<RGBStack> stacks) throws ArrangeStackException;
    
    private static void populateStack(Iterable<RGBStack> generatedImages, StackArrangement arrangement, Stack out) {

        int index = 0;
        for (RGBStack image : generatedImages) {

            BoundingBox box = arrangement.get(index++);

            // NOTE
            // For a special case where our projection z-extent is different to our actual z-extent,
            // that means
            //   we should repeat
            if (box.extent().z() != image.dimensions().z()) {

                int zShift = 0;
                do {
                    projectImageOntoStack(box, image.asStack(), out, zShift);
                    zShift += image.dimensions().z();
                } while (zShift < out.dimensions().z());

            } else {
                projectImageOntoStack(box, image.asStack(), out, 0);
            }
        }
    }
    
    /** Two {@link VoxelBuffer}s representing the source and destination of an operation. TODO use*/
    @AllArgsConstructor @Value
    private static class BufferPair {
    	VoxelBuffer<?>[] source;
    	VoxelBuffer<?>[] destination;
    }
    

    private static void projectImageOntoStack(
            BoundingBox box, Stack toProject, Stack target, int zShift) {

        assert (toProject.getNumberChannels() == target.getNumberChannels());

        Extent extent = toProject.extent();

        ReadableTuple3i leftCorner = box.cornerMin();
        int xEnd = leftCorner.x() + box.extent().x() - 1;
        int yEnd = leftCorner.y() + box.extent().y() - 1;

        int numberChannels = toProject.getNumberChannels();
        VoxelBuffer<?>[] voxelsIn = new VoxelBuffer<?>[numberChannels];
        VoxelBuffer<?>[] voxelsOut = new VoxelBuffer<?>[numberChannels];

        for (int z = 0; z < extent.z(); z++) {

            int outZ = leftCorner.z() + z + zShift;

            for (int channel = 0; channel < numberChannels; channel++) {
                voxelsIn[channel] = toProject.getChannel(channel).voxels().slice(z);
                voxelsOut[channel] = target.getChannel(channel).voxels().slice(outZ);
            }
            
            copyVoxels(leftCorner, xEnd, yEnd, target.extent(), numberChannels, voxelsIn, voxelsOut);
        }
    }
    
    private static void copyVoxels(ReadableTuple3i leftCorner, int xEnd, int yEnd, Extent target, int numberChannels, VoxelBuffer<?>[] voxelsIn, VoxelBuffer<?>[] voxelsOut) {
    	int index = 0;
        for (int y = leftCorner.y(); y <= yEnd; y++) {
            for (int x = leftCorner.x(); x <= xEnd; x++) {

                int outPos = target.offset(x, y);

                for (int channel = 0; channel < numberChannels; channel++) {
                	voxelsOut[channel].putInt(outPos, voxelsIn[channel].getInt(index));
                }
                index++;
            }
        }
    }
	
}
