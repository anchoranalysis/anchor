package org.anchoranalysis.image.io.generator.raster.obj.collection;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.generator.raster.ChnlGenerator;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectMaskCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;


/**
 * Writes objects as a Raster with unique id values for each object.
 * 
 * <p>Note that a maximum of 254 objects are allowed to be written on a channel in this way (for a 8-bit image)</p>
 * @author owen
 *
 */
public class ObjsAsUniqueValueGenerator extends ObjsGenerator {
	
	private static ChannelFactoryByte factory = new ChannelFactoryByte();
	
	public ObjsAsUniqueValueGenerator(ImageDim dim) {
		super(dim);
	}

	public ObjsAsUniqueValueGenerator(ObjectMaskCollection masks, ImageDim dim) {
		super(masks, dim);
	}
	
	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		Channel outChnl = factory.createEmptyInitialised(
			getDimensions()
		);

		VoxelBox<?> vbOutput = outChnl.getVoxelBox().any();
		
		if (getObjs().size()>254) {
			throw new OutputWriteFailedException( String.format("Collection has %d objs. A max of 254 is allowed", getObjs().size()));
		}
		
		int val = 1;
		for( ObjectMask om : getObjs() ) {
			vbOutput.setPixelsCheckMask(om, val++);
		}
		
		return new ChnlGenerator(outChnl, "maskCollection").generate();
	}
}
