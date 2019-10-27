package org.anchoranalysis.image.convert;

/*
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeShort;

import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Fraction;

public class ImgLib2Wrap {
	
	private ImgLib2Wrap() {
		// Force static access
	}
	
	public static NativeImg<? extends RealType<?>,?> wrap( VoxelBoxWrapper box, boolean do3D ) {
		
		VoxelDataType dataType = box.getVoxelDataType();
		
		if (dataType.equals(VoxelDataTypeByte.instance)) {
			return wrapByte(box.asByte(), do3D);
		} else if (dataType.equals(VoxelDataTypeShort.instance)) {
			return wrapShort(box.asShort(), do3D);
		} else if (dataType.equals(VoxelDataTypeFloat.instance)) {
				return wrapFloat(box.asFloat(), do3D);			
		} else {
			throw new IncorrectVoxelDataTypeException("Only unsigned byte and short are supported");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static NativeImg<?,?> wrap( VoxelBuffer<?> vb, Extent e) {
		
		VoxelDataType dataType = vb.dataType();
		
		if (dataType.equals(VoxelDataTypeByte.instance)) {
			return wrapByte( (VoxelBuffer<ByteBuffer>) vb, e);
		} else if (dataType.equals(VoxelDataTypeShort.instance)) {
			return wrapShort( (VoxelBuffer<ShortBuffer>) vb, e);
		} else {
			throw new IncorrectVoxelDataTypeException("Only unsigned byte and short are supported");
		}
		
	}
	
	
	public static NativeImg<UnsignedByteType,ByteArray> wrapByte( VoxelBox<ByteBuffer> box, boolean do3D ) {
		
		Extent e = box.extnt();
		
		if (!do3D) {
			return wrapByte( box.getPixelsForPlane(0), e );
		}
		
		long dim[] = new long[]{e.getX(),e.getY(),e.getZ()};
		
		PlanarImg<UnsignedByteType,ByteArray> imgOut = new PlanarImg<>(dim, new Fraction() );
		for( int z=0; z<e.getZ(); z++) {
			imgOut.setPlane(z, new ByteArray(box.getPixelsForPlane(z).buffer().array()) );
		}
				
		imgOut.setLinkedType(new UnsignedByteType(imgOut));
		return imgOut;
	}

	public static NativeImg<UnsignedShortType,ShortArray> wrapShort( VoxelBox<ShortBuffer> box, boolean do3D ) {
		
		Extent e = box.extnt();
		
		if (!do3D) {
			return wrapShort( box.getPixelsForPlane(0), e );
		}
		
		long dim[] = new long[]{e.getX(),e.getY(),e.getZ()};
		
		PlanarImg<UnsignedShortType,ShortArray> imgOut = new PlanarImg<>(dim, new Fraction() );
		for( int z=0; z<e.getZ(); z++) {
			imgOut.setPlane(z, new ShortArray(box.getPixelsForPlane(z).buffer().array()) );
		}
				
		imgOut.setLinkedType(new UnsignedShortType(imgOut));
		return imgOut;
	}
	
	
	public static NativeImg<FloatType,FloatArray> wrapFloat( VoxelBox<FloatBuffer> box, boolean do3D ) {
		
		Extent e = box.extnt();
		
		if (!do3D) {
			return wrapFloat( box.getPixelsForPlane(0), e );
		}
		
		long dim[] = new long[]{e.getX(),e.getY(),e.getZ()};
		
		PlanarImg<FloatType,FloatArray> imgOut = new PlanarImg<>(dim, new Fraction() );
		for( int z=0; z<e.getZ(); z++) {
			imgOut.setPlane(z, new FloatArray(box.getPixelsForPlane(z).buffer().array()) );
		}
				
		imgOut.setLinkedType(new FloatType(imgOut));
		return imgOut;
	}
	
	// Only uses X and Y of e, ignores Z
	public static ArrayImg<UnsignedByteType, ByteArray> wrapByte( VoxelBuffer<ByteBuffer> buffer, Extent e ) {
		long dim[] = new long[]{e.getX(),e.getY()};
		ArrayImg<UnsignedByteType, ByteArray> img = new ArrayImg<UnsignedByteType, ByteArray>(new ByteArray(buffer.buffer().array()), dim, new Fraction());
		img.setLinkedType(new UnsignedByteType(img));
		return img;
	}

	// Only uses X and Y of e, ignores Z
	public static ArrayImg<UnsignedShortType, ShortArray> wrapShort( VoxelBuffer<ShortBuffer> buffer, Extent e ) {
		long dim[] = new long[]{e.getX(),e.getY()};
		ArrayImg<UnsignedShortType, ShortArray> img = new ArrayImg<UnsignedShortType, ShortArray>(new ShortArray(buffer.buffer().array()), dim, new Fraction());
		img.setLinkedType(new UnsignedShortType(img));
		return img;
	}
	
	// Only uses X and Y of e, ignores Z
	public static ArrayImg<FloatType, FloatArray> wrapFloat( VoxelBuffer<FloatBuffer> buffer, Extent e ) {
			long dim[] = new long[]{e.getX(),e.getY()};
			ArrayImg<FloatType, FloatArray> img = new ArrayImg<FloatType, FloatArray>(new FloatArray(buffer.buffer().array()), dim, new Fraction());
			img.setLinkedType(new FloatType(img));
			return img;
		}
}
