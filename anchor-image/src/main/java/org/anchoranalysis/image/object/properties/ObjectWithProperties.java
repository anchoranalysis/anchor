package org.anchoranalysis.image.object.properties;

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
import java.util.function.Function;

import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;

/**
 * An {@link ObjectMask} with associated key-value properties.
 * 
 * @author Owen Feehan
 *
 */
public class ObjectWithProperties {

	private final Map<String,Object> properties;
	private final ObjectMask mask;
	
	public ObjectWithProperties(BoundingBox bbox ) {
		this(new ObjectMask(bbox));
	}
	
	public ObjectWithProperties( ObjectMask objMask ) {
		mask = objMask;
		properties = new HashMap<>();
	}
			
	public ObjectWithProperties( ObjectMask objMask, Map<String,Object> properties ) {
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

	/**
	 * Maps the underlying object-mask to another object-mask, reusing the same properties object.
	 * 
	 * <p>Note the properties are not duplicated, and the new object will reference the same properties</p>.
	 * 
	 * <p>This is an IMMUTABLE operation</p>
	 * 
	 * @param funcMap
	 * @return the mapped object (with identical properties) to previously.
	 */
	public ObjectWithProperties map(Function<ObjectMask,ObjectMask> funcMap) {
		return new ObjectWithProperties(
			funcMap.apply(mask),
			properties
		);
	}
	
	public ObjectWithProperties duplicate() {
		ObjectWithProperties out = new ObjectWithProperties( mask.duplicate() );
		for( String key : properties.keySet()) {
			out.properties.put(key, properties.get(key) );
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

	public String toString() {
		return mask.toString();
	}

	public ObjectMask getMask() {
		return mask;
	}

	public BinaryValuesByte getBinaryValues() {
		return mask.getBinaryValuesByte();
	}

	public Map<String, Object> getProperties() {
		return properties;
	}
}
