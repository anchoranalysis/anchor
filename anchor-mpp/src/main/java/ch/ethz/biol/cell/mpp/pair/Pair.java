package ch.ethz.biol.cell.mpp.pair;

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

import org.anchoranalysis.anchor.overlay.id.Identifiable;

public class Pair<ItemType extends Identifiable> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7867852145124991678L;
	private final ItemType source;
	private final ItemType destination;
	
	public Pair(ItemType source, ItemType destination ) {
		super();
		
		if (source.getId() < destination.getId()) {
			this.source = source;
			this.destination = destination;
		} else {
			this.destination = source;
			this.source = destination;
		}
	}
	    	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals( Object othero ) {
		
		if (othero==null) {
			return false;
		}
		if (othero== this) {
	        return true;
		}
	    if (!(othero instanceof Pair)) {
	        return false;
	    }
		
		Pair<ItemType> other = (Pair<ItemType>) othero;
		return ((this.source.equals(other.source)) && (this.destination.equals(other.destination)));
	}
	
	@Override
	public int hashCode() { 
		if (source==null || destination==null) {
			return 0;
		}
		
		return (source.getId() * 3) + destination.getId();
  	}

	public ItemType getSource() {
		return source;
	}

	public ItemType getDestination() {
		return destination;
	}

	@Override
	public String toString() {
		return String.format("%d--%d", source.getId(), destination.getId() );
	}
}