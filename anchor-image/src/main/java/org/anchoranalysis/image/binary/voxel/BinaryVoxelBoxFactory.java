package org.anchoranalysis.image.binary.voxel;

import java.nio.ByteBuffer;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;

public class BinaryVoxelBoxFactory {
	
	private static BinaryVoxelBoxFactory instance;
	
	/** Singleton */
	public static BinaryVoxelBoxFactory instance() {
		if (instance==null) {
			instance = new BinaryVoxelBoxFactory();
		}
		return instance;
	}
	
	/**
	 * Creates a binary-voxel box of a given size using default factory and unsigned byte type
	 * 
	 * @param extent the size of the voxel-box
	 * @return a new created empty binary voxel box of specified size (all voxels initialized to 0)
	 * @throws CreateException
	 */
	@SuppressWarnings("unchecked")
	public BinaryVoxelBox<ByteBuffer> create( Extent extent ) throws CreateException  {
		return (BinaryVoxelBox<ByteBuffer>) create(extent, VoxelDataTypeUnsignedByte.instance, BinaryValues.getDefault() );
	}
	
	
	/**
	 * Creates an empty binary-value-box (all voxels initialized to 0)
	 * 
	 * @param extent the size of the voxel-box
	 * @param dataType the data-type of the underlying voxel-buffer, either unsigned-byte or unsigned-int
	 * @param binaryValues what voxel-values constitutes OFF and ON
	 * @return a new created empty binary voxel box of specified size (all voxels initialized to 0)
	 * @throws CreateException
	 */
	public BinaryVoxelBox<?> create( Extent extent, VoxelDataType dataType, BinaryValues binaryValues ) throws CreateException  {
		if (dataType.equals(VoxelDataTypeUnsignedByte.instance)) {
			return new BinaryVoxelBoxByte( VoxelBoxFactory.instance().getByte().create(extent), binaryValues );
		} else if (dataType.equals(VoxelDataTypeUnsignedInt.instance)) {
			return new BinaryVoxelBoxInt( VoxelBoxFactory.instance().getInt().create(extent), binaryValues );
		} else {
			throw new CreateException("Unsupported voxel-data-type, only unsigned byte and int are supported");
		}
	}
}
