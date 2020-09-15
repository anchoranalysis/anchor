package org.anchoranalysis.core.graph;

/*
 * #%L
 * anchor-core
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

import com.google.common.collect.HashBasedTable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * A graph, either directed or undirected, with edges containing a payload of type {@code E}.
 *
 * @author Owen Feehan
 * @param <V> vertex-type
 * @param <P> edge payload-type
 */
public class GraphWithPayload<V, P> {

    /** The vertices in the graph. */
    private HashSet<V> vertices;

    /**
     * A table for storing the edges in a given direction. Rows index outgoing edges. Columns index
     * incoming edges.
     */
    private HashBasedTable<V, V, TypedEdge<V, P>> edges;

    /** If true, it's an undirected graph, otherwise directed graph. */
    private boolean undirected = true;

    /**
     * Creates the graph.
     *
     * @param undirected true if it should be an undirected graph (an edge applies in both
     *     directions), false if it should be a directed graph (an edge applies in one direction
     *     only).
     */
    public GraphWithPayload(boolean undirected) {
        this.edges = HashBasedTable.create();
        this.vertices = new HashSet<>();
        this.undirected = undirected;
    }

    /**
     * Creates a new graph with identical elements and structure, reusing the existing vertice and
     * edge data objects.
     *
     * @return a newly created graph, with newly created vertices and edges, but reusing the
     *     data-objects of tyoe {@code V} and {@code E}.
     */
    @SuppressWarnings("unchecked")
    public GraphWithPayload<V, P> shallowCopy() {
        GraphWithPayload<V, P> out = new GraphWithPayload<>(undirected);
        out.vertices = (HashSet<V>) vertices.clone();
        out.edges = HashBasedTable.create(edges);
        return out;
    }

    /**
     * The set of all vertices in the graph.
     *
     * @return the set (as is used internally within the class, without any duplication).
     */
    public Set<V> vertices() {
        return vertices;
    }

    /**
     * The number of vertices in the graph.
     *
     * @return the number of vertices
     */
    public int numberVertices() {
        return vertices.size();
    }

    /**
     * Does the graph contain a particular vertex?
     *
     * @param vertex the vertex to check if it is contained
     * @return true iff the graph contains the vertex
     */
    public boolean containsVertex(V vertex) {
        return vertices.contains(vertex);
    }

    /**
     * Adds a vertex.
     *
     * @param vertex the vertex to add
     */
    public void addVertex(V vertex) {
        vertices.add(vertex);
    }

    /**
     * Removes a vertex and any edges connected to it.
     *
     * @param vertex the vertex to remove
     * @throws OperationFailedException if the vertex doesn't exist in the graph.
     */
    public void removeVertex(V vertex) throws OperationFailedException {

        if (!vertices.remove(vertex)) {
            throw new OperationFailedException(
                    String.format(
                            "A vertex cannot be removed, because it does not exist in the graph: %s",
                            vertex));
        }

        edges.row(vertex).clear();
        edges.column(vertex).clear();
    }

    /**
     * Add an edge between two vertices.
     *
     * <p>For an undirected graph, the directionality is irrelevant, and will achieve the same
     * effect, whatever the order of {@code from} and {@code to}.
     *
     * @param from the vertex the edge joins <i>from</i>.
     * @param to the vertex the edge joins <i>to</i>.
     * @param edgePayload the payload for the edge.
     */
    public void addEdge(V from, V to, P edgePayload) {
        TypedEdge<V, P> edgeWithVertices = new TypedEdge<>(edgePayload, from, to);
        edges.put(from, to, edgeWithVertices);
        if (undirected) {
            edges.put(to, from, edgeWithVertices);
        }
    }

    /**
     * Remove an edge between two vertices.
     *
     * <p>For an undirected graph, the directionality is irrelevant, and will achieve the same
     * effect, whatever the order of {@code from} and {@code to}.
     *
     * @param from the vertex the edge joins <i>from</i>.
     * @param to the vertex the edge joins <i>to</i>.
     */
    public void removeEdge(V from, V to) {
        edges.remove(from, to);
        if (undirected) {
            edges.remove(to, from);
        }
    }

    /**
     * The edges in the graph, all of them, without any duplicates.
     *
     * <p>This operation is more expensive than {@link #edgesMaybeDuplicates()} but guarantees that
     * no edge is repeated.
     *
     * @return a newly created set containing the edges of the graph.
     */
    public Set<TypedEdge<V, P>> edgesUnique() {
        return edgesMaybeDuplicates().stream().collect(Collectors.toSet());
    }

    /**
     * The edges in the graph, all of them, but with some edges possibly duplicated.
     *
     * <p>This operation is more cheaper than {@link #edgesUnique()} but edges may exist twice.
     *
     * @return the collection (as exists internally) of edges in the graph, with some edges may
     *     exist twice.
     */
    public Collection<TypedEdge<V, P>> edgesMaybeDuplicates() {
        return edges.values();
    }

    /**
     * The vertices that are connected to a particular vertex by an outgoing edge
     *
     * @param vertex the vertex to find adjacent vertices for
     * @return all vertices to which an outgoing edge exists from {@code vertex}
     */
    public Collection<V> adjacentVerticesOutgoing(V vertex) {
        Collection<TypedEdge<V, P>> edgesForVertex = outgoingEdgesFor(vertex);
        return edgesForVertex.stream()
                .map(edge -> edge.otherVertex(vertex))
                .collect(Collectors.toList());
    }

    /**
     * All outgoing edges for a given vertex.
     *
     * @param vertex the vertex
     * @return a collection (based on an internal data structure, unduplicated) of outgoing edges
     *     for the vertex.
     */
    public Collection<TypedEdge<V, P>> outgoingEdgesFor(V vertex) {
        return edges.row(vertex).values();
    }

    @Override
    public String toString() {
        return describe(true);
    }

    /**
     * Describes the graph in a multi-line string.
     *
     * @param includeEdgePayload whether to show the payload of each edge
     * @return a multi-line string describing the graph
     */
    public String describe(boolean includeEdgePayload) {
        StringBuilder builder = new StringBuilder();

        builder.append("<graph>");
        builder.append(System.lineSeparator());

        for (V vertex : vertices) {

            builder.append("Vertex: ");
            builder.append(vertex);
            builder.append("\t");

            appendEdgesDescription(builder, vertex, includeEdgePayload);

            builder.append(System.lineSeparator());
        }

        builder.append("</graph>");
        builder.append(System.lineSeparator());

        return builder.toString();
    }

    /** Appends a description of the edges of a vertex to a string-builder. */
    private void appendEdgesDescription(StringBuilder builder, V vertex, boolean includePayload) {
        boolean first = true;
        for (TypedEdge<V, P> edgeWith : outgoingEdgesFor(vertex)) {

            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }

            builder.append(edgeWith.describeTo(includePayload));
        }
    }
}
