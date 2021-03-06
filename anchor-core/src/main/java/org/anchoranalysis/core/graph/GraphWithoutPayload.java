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
import javax.lang.model.type.NullType;
import org.anchoranalysis.core.exception.OperationFailedException;

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
     * The vertices that are connected to a particular vertex by an outgoing edge
     *
     * @param vertex the vertex to find adjacent vertices for
     * @return all vertices to which an outgoing edge exists from {@code vertex}
     */
    public Collection<V> adjacentVerticesOutgoing(V vertex) {
        return delegate.adjacentVerticesOutgoing(vertex);
    }
}
