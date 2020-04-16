package org.anchoranalysis.image.factory;

import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

/**
 * Multiplexes betwen four different types of factories each representing a particular primitive type
 * 
 * @author Owen Feehan
 *
 * @param <T>
 */
public abstract class VoxelDataTypeFactoryMultiplexer<T> {

	private T factoryByte;
	private T factoryShort;
	private T factoryInt;
	private T factoryFloat;
	
	public VoxelDataTypeFactoryMultiplexer(T factoryByte, T factoryShort, T factoryInt, T factoryFloat) {
		super();
		this.factoryByte = factoryByte;
		this.factoryShort = factoryShort;
		this.factoryInt = factoryInt;
		this.factoryFloat = factoryFloat;
	}
	
	/**
	 * Multiplexes one of the factories according to data-type
	 * @param dataType the type to find a factory for
	 * @return a factory if it exists, or else an exception
	 */
	public T get( VoxelDataType dataType ) {
		
		if (dataType.equals(VoxelDataTypeUnsignedByte.instance)) {
			return factoryByte;
		} else if (dataType.equals(VoxelDataTypeUnsignedShort.instance)) {
			return factoryShort;
		} else if (dataType.equals(VoxelDataTypeUnsignedInt.instance)) {
			return factoryInt;
		} else if (dataType.equals(VoxelDataTypeFloat.instance)) {
			return factoryFloat;
		} else {
			throw new IncorrectVoxelDataTypeException("Non-existent type");
		}
	}
}
