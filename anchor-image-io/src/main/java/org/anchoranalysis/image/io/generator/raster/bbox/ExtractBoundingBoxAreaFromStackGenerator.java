/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.generator.raster.bbox;

import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * An iterable-generator that outputs the portion of a stack corresponding to a bounding-box
 * <p>
 * If the stack isn't defined, an empty grayscale (unsigned 8-bit) channel is returned of same-size as the bounding-box.
 * @author Owen Feehan
 *
 */
public class ExtractBoundingBoxAreaFromStackGenerator extends RasterGenerator
        implements IterableObjectGenerator<BoundingBox, Stack> {

    private static final String MANIFEST_FUNCTION = "boundingBoxExtract";
    
    private static final ChannelFactoryByte CHANNEL_FACTORY = new ChannelFactoryByte();
    
    // START REQUIRED ARGUMENTS
    private final Optional<Stack> stack;
    // END REQUIRED ARGUMENTS
    
    private BoundingBox bbox;

    public ExtractBoundingBoxAreaFromStackGenerator(Optional<Stack> stack) {
        this.stack = stack;
    }    
    
    @Override
    public Stack generate() throws OutputWriteFailedException {

        if (getIterableElement() == null) {
            throw new OutputWriteFailedException("no mutable element set");
        }

        try {
            if (stack.isPresent()) {
                return createExtract(stack.get());
            } else {
                return new Stack( createEmptyGrayscaleChannelForCurrentElement() );
            }
        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public BoundingBox getIterableElement() {
        return bbox;
    }

    @Override
    public void setIterableElement(BoundingBox element) {
        this.bbox = element;
    }

    @Override
    public ObjectGenerator<Stack> getGenerator() {
        return this;
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", MANIFEST_FUNCTION));
    }

    @Override
    public boolean isRGB() {
        return stack.isPresent() && stack.get().getNumberChannels() == 3;
    }

    private Stack createExtract(Stack stackIn) throws CreateException {
        Stack stackOut = new Stack();

        for (Channel chnlIn : stackIn) {

            VoxelBox<?> vbIn = chnlIn.getVoxelBox().any();

            VoxelBox<?> vbExtracted = vbIn.region(bbox, false);

            Channel chnlExtracted =
                    ChannelFactory.instance().create(vbExtracted, stackIn.getDimensions().getRes());
            try {
                stackOut.addChannel(chnlExtracted);
            } catch (IncorrectImageSizeException e) {
                throw new CreateException(e);
            }
        }

        return stackOut;
    }
    
    private Channel createEmptyGrayscaleChannelForCurrentElement() {
        ImageDimensions dimensions = new ImageDimensions(bbox.extent());
        return CHANNEL_FACTORY.createEmptyInitialised(dimensions);
    }
}
