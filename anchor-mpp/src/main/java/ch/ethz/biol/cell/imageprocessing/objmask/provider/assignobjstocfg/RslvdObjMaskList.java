package ch.ethz.biol.cell.imageprocessing.objmask.provider.assignobjstocfg;

/*
 * #%L
 * anchor-mpp
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

import org.anchoranalysis.image.objmask.ObjMaskCollection;

public class RslvdObjMaskList implements Iterable<RslvdObjMask> {
	private List<RslvdObjMask> delegate;
	
	public RslvdObjMaskList() {
		delegate = new ArrayList<>();
	}

	public void add(RslvdObjMask element) {
		delegate.add(element);
	}

	public Iterator<RslvdObjMask> iterator() {
		return delegate.iterator();
	}
	
	public void addAllTo( ObjMaskCollection objsOut ) {
		for( RslvdObjMask rom : delegate ) {
			objsOut.add( rom.getObjMask() );
		}
	}

	public int size() {
		return delegate.size();
	}
	
	public ObjMaskCollection createObjs() {
		ObjMaskCollection out = new ObjMaskCollection();
		for( RslvdObjMask rom : this) {
			out.add(rom.getObjMask());
		}
		return out;
	}
	
}