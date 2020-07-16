/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.obj.collection;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.generator.raster.ChnlGenerator;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Writes objects as a Raster with unique id values for each object.
 *
 * <p>Note that a maximum of 254 objects are allowed to be written on a channel in this way (for a
 * 8-bit image)
 *
 * @author Owen Feehan
 */
public class ObjectsAsUniqueValueGenerator extends ObjectsGenerator {

    private static ChannelFactoryByte factory = new ChannelFactoryByte();

    public ObjectsAsUniqueValueGenerator(ImageDimensions dimensions) {
        super(dimensions);
    }

    public ObjectsAsUniqueValueGenerator(ImageDimensions dimensions, ObjectCollection masks) {
        super(dimensions, masks);
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {

        Channel outChnl = factory.createEmptyInitialised(getDimensions());

        VoxelBox<?> vbOutput = outChnl.getVoxelBox().any();

        if (getObjects().size() > 254) {
            throw new OutputWriteFailedException(
                    String.format(
                            "Collection has %d objects. A max of 254 is allowed",
                            getObjects().size()));
        }

        int val = 1;

        for (ObjectMask object : getObjects()) {
            vbOutput.setPixelsCheckMask(object, val++);
        }

        return new ChnlGenerator(outChnl, "maskCollection").generate();
    }
}
