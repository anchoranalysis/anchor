package org.anchoranalysis.core.graph;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

public class EdgeTypeWithVertices<V,E> {
	
	private E edge;
	private V node1;
	private V node2;
	
	public EdgeTypeWithVertices(E edge, V node1,
			V node2) {
		super();
		this.edge = edge;
		this.node1 = node1;
		this.node2 = node2;
	}

	public E getEdge() {
		return edge;
	}

	public V getNode1() {
		return node1;
	}

	public V getNode2() {
		return node2;
	}
	

	// Returns the OTHER vertex on the edge i.e. the one that isn't vertex 
	public V otherVertex( V vertex ) {
		if (node1==vertex) {
			return node2;
		} else {
			return node1;
		}
	}
}
