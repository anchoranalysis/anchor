package org.anchoranalysis.image.io.objs;

/*-
 * #%L
 * anchor-image-io
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.nio.ByteBuffer;
import java.util.function.ToIntFunction;

import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;

import ch.systemsx.cisd.base.mdarray.MDByteArray;
import ch.systemsx.cisd.hdf5.HDF5IntStorageFeatures;
import ch.systemsx.cisd.hdf5.IHDF5Writer;

/**
 * Writes an ObjectMask to a path within a HDF5 file
 *   The mask is written as a 3D array of 255 and 0 bytes
 *   The corner-position of the bounding box is added as attributes: x, y, z
 *   
 * @author Owen Feehan
 *
 */
class ObjectMaskHDF5Writer {

	private ObjectMask obj;
	private String pathHDF5;
	private IHDF5Writer writer;
	private boolean compression;
	
	/**
	 * Constructor
	 * 
	 * @param objectMask object-mask to write
	 * @param pathHDF5 the path in the HDF5 file to write it to
	 * @param writer an opened-writer for the HDF5 file
	 */
	public ObjectMaskHDF5Writer(ObjectMask objectMask, String pathHDF5, IHDF5Writer writer, boolean compression) {
		super();
		this.obj = objectMask;
		this.pathHDF5 = pathHDF5;
		this.writer = writer;
		this.compression = compression;
	}

	private HDF5IntStorageFeatures compressionLevel() {
		if (compression) {
			return HDF5IntStorageFeatures.INT_DEFLATE_UNSIGNED;
		} else {
			return HDF5IntStorageFeatures.INT_NO_COMPRESSION_UNSIGNED;
		}
	}
	
	public void apply() {
		
		writer.uint8().writeMDArray(
			pathHDF5,
			byteArray( obj.binaryVoxelBox() ),
			compressionLevel()
		);
		
		addCrnr();
	}
	
	private void addCrnr() {
		addAttr("x", ReadableTuple3i::getX );
		addAttr("y", ReadableTuple3i::getY );
		addAttr("z", ReadableTuple3i::getZ );
	}
	
	private void addAttr( String attrName, ToIntFunction<ReadableTuple3i> extrVal) {
		
		Integer crnrVal = extrVal.applyAsInt( obj.getBoundingBox().cornerMin() );
		writer.uint32().setAttr(
			pathHDF5,
			attrName,
			crnrVal.intValue()
		);
	}
		
	private static MDByteArray byteArray( BinaryVoxelBox<ByteBuffer> bvb ) {

		Extent e = bvb.extent();
		
		MDByteArray md = new MDByteArray( dimsFromExtnt(e) );
		
		for( int z=0; z<e.getZ(); z++) {
		
			ByteBuffer bb = bvb.getPixelsForPlane(z).buffer();
						
			for( int y=0; y<e.getY(); y++) {
				for( int x=0; x<e.getX(); x++) {
					md.set(bb.get(e.offset(x, y)), x, y, z);
				}
			}
		}
		return md;
	}
	
	private static int[] dimsFromExtnt( Extent e ) {
		int[] adims = { e.getX(), e.getY(), e.getZ() };
		return adims;
	}
}
