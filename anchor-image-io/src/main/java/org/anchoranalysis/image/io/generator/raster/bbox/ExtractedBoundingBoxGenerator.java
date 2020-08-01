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
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class ExtractedBoundingBoxGenerator extends RasterGenerator
        implements IterableObjectGenerator<BoundingBox, Stack> {

    // START REQUIRED ARGUMENTS
    private final Stack stack;
    private final String manifestFunction;
    // END REQUIRED ARGUMENTS
    
    private BoundingBox bbox;

    public ExtractedBoundingBoxGenerator(Stack stack, String manifestFunction) {
        super();
        this.stack = stack;
        this.manifestFunction = manifestFunction;
    }    
    
    @Override
    public Stack generate() throws OutputWriteFailedException {

        if (getIterableElement() == null) {
            throw new OutputWriteFailedException("no mutable element set");
        }

        try {
            return createExtract(stack);
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
        return Optional.of(new ManifestDescription("raster", manifestFunction));
    }

    @Override
    public boolean isRGB() {
        return stack.getNumberChannels() == 3;
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
}
