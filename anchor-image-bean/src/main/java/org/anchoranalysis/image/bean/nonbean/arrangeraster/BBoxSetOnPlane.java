package org.anchoranalysis.image.bean.nonbean.arrangeraster;

/*
 * #%L
 * anchor-image-io
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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;

// Describes a set of bounding boxes on top of a plane
public class BBoxSetOnPlane implements Iterable<BoundingBox> {

	private Extent extent;
	
	private List<BoundingBox> list = new ArrayList<>();
	
	public BBoxSetOnPlane(Extent extent) {
		super();
		this.extent = extent;
	}
	
	public BBoxSetOnPlane(Extent extent, BoundingBox bbox) {
		this(extent);
		add(bbox);
	}
	
	public void add(BoundingBox obj) {
		list.add(obj);
	}
	
	public BoundingBox get(int index) {
		return list.get(index);
	}

	public Iterator<BoundingBox> bboxIterator() {
		return list.iterator();
	}

	@Override
	public Iterator<BoundingBox> iterator() {
		return bboxIterator();
	}

	public Extent getExtnt() {
		return extent;
	}
	
}
