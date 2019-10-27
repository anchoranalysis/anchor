package ch.ethz.biol.cell.mpp.nrg;

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


import java.util.HashMap;
import java.util.Iterator;

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

import ch.ethz.biol.cell.mpp.nrg.nrgscheme.NRGScheme;

/**
 * A set of NRGSchemes each with a name.
 * 
 * SharedFeatures and a CachedCalculationList are also associated
 * 
 * @author Owen Feehan
 *
 */
public class NamedNRGSchemeSet implements Iterable<NamedBean<NRGScheme>> {

	private HashMap<String,NamedBean<NRGScheme>> delegate = new HashMap<>();
	private SharedFeatureSet sharedFeatures;
	
	public NamedNRGSchemeSet(SharedFeatureSet sharedFeatures) {
		super();
		this.sharedFeatures = sharedFeatures;
	}

	public SharedFeatureSet getSharedFeatures() {
		return sharedFeatures;
	}
	public void setSharedFeatures(SharedFeatureSet sharedFeatures) {
		this.sharedFeatures = sharedFeatures;
	}
	
	public boolean add(String name, NRGScheme nrgScheme) {
		assert nrgScheme.getRegionMap()!=null;
		delegate.put(name, new NamedBean<NRGScheme>(name, nrgScheme) );
		return true;
	}
	
	public NamedBean<NRGScheme> get(String name) {
		return delegate.get(name);
	}
	
	@Override
	public Iterator<NamedBean<NRGScheme>> iterator() {
		return delegate.values().iterator();
	}
		
}
