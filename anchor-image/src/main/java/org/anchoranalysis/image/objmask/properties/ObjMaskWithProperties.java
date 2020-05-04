package org.anchoranalysis.image.objmask.properties;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class ObjMaskWithProperties {

	private Map<String,Object> properties = new HashMap<>();
	private ObjMask mask;
	
	public ObjMaskWithProperties(BoundingBox BoundingBox ) {
		mask = new ObjMask(BoundingBox);
	}
	
	public ObjMaskWithProperties( ObjMask objMask ) {
		mask = objMask;
	}
	
	public ObjMaskWithProperties( ObjMask objMask, Map<String,Object> properties ) {
		mask = objMask;
		this.properties = properties;
	}
	
	public void setProperty( String name, Object value ) {
		properties.put(name, value);
	}
	
	public Object getProperty( String name ) {
		return properties.get(name);
	}
	
	public boolean hasProperty( String name ) {
		return properties.containsKey(name);
	}

	public void convertToMaxIntensityProjection() {
		mask.convertToMaxIntensityProjection();
	}

	public ObjMaskWithProperties growBuffer(Point3i neg, Point3i pos, Extent clipRegion) throws OperationFailedException {
		return new ObjMaskWithProperties(
			mask.growBuffer(
				neg,
				pos,
				Optional.of(clipRegion)
			),
			properties
		);
	}
	
	public ObjMaskWithProperties duplicate() {
		ObjMaskWithProperties out = new ObjMaskWithProperties( mask.duplicate() );
		for( String key : properties.keySet()) {
			out.properties.put( new String(key), properties.get(key) );
		}
		return out;
	}

	public boolean equals(Object obj) {
		return mask.equals(obj);
	}

	public BoundingBox getBoundingBox() {
		return mask.getBoundingBox();
	}

	public VoxelBox<ByteBuffer> getVoxelBox() {
		return mask.getVoxelBox();
	}

	public int hashCode() {
		return mask.hashCode();
	}

	public void setBoundingBox(BoundingBox BoundingBox) {
		mask.setBoundingBox(BoundingBox);
	}

	public boolean sizesMatch() {
		return mask.sizesMatch();
	}

	public void setVoxelBox(VoxelBox<ByteBuffer> voxelBox) {
		mask.setVoxelBox(voxelBox);
	}

	public String toString() {
		return mask.toString();
	}

	public ObjMask getMask() {
		return mask;
	}

	public BinaryValuesByte getBinaryValues() {
		return mask.getBinaryValuesByte();
	}

	public Map<String, Object> getProperties() {
		return properties;
	}
}
