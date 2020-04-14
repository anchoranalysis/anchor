package org.anchoranalysis.core.graph;

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
		assert node1!=node2;
		if (node1==vertex) {
			return node2;
		} else {
			assert node2==vertex;
			return node1;
		}
	}
}