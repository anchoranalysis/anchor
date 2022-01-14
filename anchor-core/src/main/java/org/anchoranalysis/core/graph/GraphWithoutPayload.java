/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.core.graph;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.lang.model.type.NullType;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;

/**
 * A graph, either directed or undirected, where edges contain no payload.
 *
 * @author Owen Feehan
 * @param <V> vertex-type
 */
public class GraphWithoutPayload<V> {

    /** As an implementation detail, a graph is created with a payload that is always null. */
    private final GraphWithPayload<V, NullType> delegate;

    /**
     * Creates the graph.
     *
     * @param undirected true if it should be an undirected graph (an edge applies in both
     *     directions), false if it should be a directed graph (an edge applies in one direction
     *     only).
     */
    public GraphWithoutPayload(boolean undirected) {
        this.delegate = new GraphWithPayload<>(undirected);
    }

    /**
     * Does the graph contain a particular vertex?
     *
     * @param vertex the vertex to check if it is contained
     * @return true iff the graph contains the vertex
     */
    public boolean containsVertex(V vertex) {
        return delegate.containsVertex(vertex);
    }

    /**
     * Does the graph contain a particular edge?
     *
     * @param from the vertex the edge emanates <i>from</i>.
     * @param to the vertex the edge is connected <i>to</i>.
     * @return true iff an edge exists from {@code from} to {@code to}.
     */
    public boolean containsEdge(V from, V to) {
        return delegate.containsEdge(from, to);
    }

    /**
     * Adds a vertex.
     *
     * @param vertex the vertex to add
     */
    public void addVertex(V vertex) {
        delegate.addVertex(vertex);
    }

    /**
     * Removes a vertex and any edges connected to it.
     *
     * @param vertex the vertex to remove
     * @throws OperationFailedException if the vertex doesn't exist in the graph.
     */
    public void removeVertex(V vertex) throws OperationFailedException {
        delegate.removeVertex(vertex);
    }

    /**
     * Add an edge between two vertices.
     *
     * <p>For an undirected graph, the directionality is irrelevant, and will achieve the same
     * effect, whatever the order of {@code from} and {@code to}.
     *
     * <p>No error is reported if an edge already exists.
     *
     * @param from the vertex the edge joins <i>from</i>.
     * @param to the vertex the edge joins <i>to</i>.
     */
    public void addEdge(V from, V to) {
        delegate.addEdge(from, to, null);
    }

    /**
     * Adds edge(s) from the vertex {@code from} to each element in {@code toCollection}.
     *
     * <p>This creates as many edges as exist elements in {@code toCollection}, unless some such
     * edges already exist.
     *
     * <p>No error is reported if an edge already exists.
     *
     * @param from the vertex the edge joins <i>from</i>.
     * @param toCollection the vertex the edge joins <i>to</i>, for each intended edge.
     */
    public void addEdges(V from, Collection<V> toCollection) {
        for (V other : toCollection) {
            addEdge(from, other);
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
        delegate.removeEdge(from, to);
    }

    @Override
    public String toString() {
        return delegate.describe(false);
    }

    /**
     * The vertices that are connected to a particular vertex by an outgoing edge.
     *
     * @param vertex the vertex to find adjacent vertices for.
     * @return all vertices to which an outgoing edge exists from {@code vertex}.
     */
    public List<V> adjacentVerticesOutgoing(V vertex) {
        return delegate.adjacentVerticesOutgoing(vertex);
    }

    /**
     * Like {@link #adjacentVerticesOutgoing} but returns a {@link Stream} instead of a {@link Set}.
     *
     * @param vertex the vertex to find adjacent vertices for.
     * @return all vertices to which an outgoing edge exists from {@code vertex}.
     */
    public Stream<V> adjacentVerticesOutgoingStream(V vertex) {
        return delegate.adjacentVerticesOutgoingStream(vertex);
    }

    /**
     * Merges two existing vertices together.
     *
     * <p>The two existing vertices are replaced with {@code merged}.
     *
     * <p>Existing incoming and outgoing edges for the two vertices are then connected instead to
     * {@code merged}.
     *
     * @param element1 the first element to merge.
     * @param element2 the second element to merge.
     * @param merged the merged element that replaces {@code element1} and {@code element2}.
     */
    public void mergeVertices(V element1, V element2, V merged) {

        Collection<V> adjacentSource = adjacentVerticesOutgoing(element1);
        Collection<V> adjacentOverlapping = adjacentVerticesOutgoing(element2);

        try {
            removeVertex(element1);
            removeVertex(element2);
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }

        addVertex(merged);

        addEdges(merged, adjacentSource);
        addEdges(merged, adjacentOverlapping);
    }

    /**
     * The number of vertices in the graph.
     *
     * @return the number of vertices
     */
    public int numberVertices() {
        return delegate.numberVertices();
    }

    /**
     * The number of edges in the graph.
     *
     * @return the number of edges
     */
    public int numberEdges() {
        return delegate.numberEdges();
    }

    /**
     * The set of all vertices in the graph.
     *
     * @return the set (as is used internally within the class, without any duplication).
     */
    public Set<V> vertices() {
        return delegate.vertices();
    }

    /**
     * Describes the graph in a multi-line string.
     *
     * @return a multi-line string describing the graph
     */
    public String describe() {
        return delegate.describe(false);
    }
}
