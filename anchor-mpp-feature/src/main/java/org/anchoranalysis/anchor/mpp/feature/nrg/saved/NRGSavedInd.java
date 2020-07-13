package org.anchoranalysis.anchor.mpp.feature.nrg.saved;

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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.feature.nrg.NRGTotal;

public class NRGSavedInd implements Serializable, Iterable<NRGTotal> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5205697546115728135L;

	// Pairwise total
    private double nrgTotal;
    
    private ArrayList<NRGTotal> ind;
	
	public NRGSavedInd shallowCopy() {
		
		NRGSavedInd out = new NRGSavedInd();
		
		// Copy each item directly
    	out.ind = new ArrayList<>();
    	this.ind .stream().forEach(out.ind::add);
    	out.nrgTotal = this.nrgTotal;
    	return out;
	}
	
	public NRGSavedInd deepCopy() {
		
		NRGSavedInd out = new NRGSavedInd();
		
		// Copy each item directly
    	out.ind = new ArrayList<>();
    	ind.forEach( indComponent->
    		out.ind.add( indComponent.deepCopy() )
    	);
    	out.nrgTotal = this.nrgTotal;
    	return out;
	}
	

	public void setNrgTotal(double nrgTotal) {
		this.nrgTotal = nrgTotal;
	}

	public int size() {
		return ind.size();
	}

	public double getNrgTotal() {
		return nrgTotal;
	}
	
	public NRGTotal get( int index ) {
		return this.ind.get(index);
	}
	
	
	public void resetInd() {
		this.ind = new ArrayList<>();
	}
	
	public void add( NRGTotal item ) {
		this.ind.add( item );
		this.nrgTotal += item.getTotal();
	}
	
	public void rmv( int index ) {
		// We calculate it's individual contribution
		this.nrgTotal -= this.ind.get(index).getTotal();
		this.ind.remove(index);		
	}
	
	public void exchange( int index, NRGTotal item ) {
		
		// we make the necessary changes on the individual components
		this.nrgTotal -= get(index).getTotal();
		this.nrgTotal += item.getTotal();
		this.ind.set(index, item);		
	}
	
	public String stringCfgNRG( Cfg cfg ) {
		
		String newLine = System.getProperty("line.separator");
		StringBuilder s = new StringBuilder("{");
		s.append(newLine);
		
		// We loop through each item and write out the configuration AND its associated individual energy
		int i=0;
		Iterator<NRGTotal> iteratorNRG = ind.iterator();
		for(Iterator<Mark> iteratorMark = cfg.iterator(); iteratorMark.hasNext(); ) {
			assert iteratorNRG.hasNext();
			s.append( String.format("%d. %s    nrg=%e%n", i++, iteratorMark.next().toString(), iteratorNRG.next().getTotal()) );
		}
		
		s.append("}");
		s.append(newLine);
		
		return s.toString();
	}
	
	public void assertValid() {
		assert !Double.isNaN( nrgTotal );
	}
	
	@Override
	public Iterator<NRGTotal> iterator() {
		return ind.iterator();
	}
}
