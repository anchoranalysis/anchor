/* (C)2020 */
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Owen Feehan
 * @param <V> Vertex-type
 * @param <E> Edge-type
 */
public class GraphWithEdgeTypes<V, E> {

    private HashSet<V> setVertices;
    private HashBasedTable<V, V, EdgeTypeWithVertices<V, E>> tableEdge;

    // If TRUE it's an undirected graph, otherwise directed graph
    private boolean undirected = true;

    public GraphWithEdgeTypes(boolean undirected) {
        this.tableEdge = HashBasedTable.create();
        this.setVertices = new HashSet<>();
        this.undirected = undirected;
    }

    @SuppressWarnings("unchecked")
    public GraphWithEdgeTypes<V, E> shallowCopy() {
        GraphWithEdgeTypes<V, E> out = new GraphWithEdgeTypes<>(undirected);
        out.setVertices = (HashSet<V>) setVertices.clone();
        out.tableEdge = HashBasedTable.create(tableEdge);
        return out;
    }

    // Edges may contain duplicates
    public Collection<EdgeTypeWithVertices<V, E>> edgeSetWithPossibleDuplicates() {
        return tableEdge.values();
    }

    // Edges are only listed once
    public Collection<EdgeTypeWithVertices<V, E>> edgeSetUnique() {
        Set<EdgeTypeWithVertices<V, E>> unique = new HashSet<>();
        for (EdgeTypeWithVertices<V, E> edge : edgeSetWithPossibleDuplicates()) {
            unique.add(edge);
        }
        return unique;
    }

    public Collection<V> adjacentVertices(V node) {
        Collection<EdgeTypeWithVertices<V, E>> edges = edgesOf(node);

        List<V> listOut = new ArrayList<>();
        for (EdgeTypeWithVertices<V, E> e : edges) {
            listOut.add(e.otherVertex(node));
        }
        return listOut;
    }

    public Collection<EdgeTypeWithVertices<V, E>> edgesOf(V node) {
        return tableEdge.row(node).values();
    }

    public boolean containsVertex(V node) {
        return setVertices.contains(node);
    }

    public Collection<V> vertexSet() {
        return setVertices;
    }

    public void addVertex(V node) {
        setVertices.add(node);
    }

    public void removeVertex(V node) {
        setVertices.remove(node);

        tableEdge.row(node).clear();
        tableEdge.column(node).clear();
    }

    public void addEdge(V node1, V node2, E edge) {
        EdgeTypeWithVertices<V, E> edgeWithVertices =
                new EdgeTypeWithVertices<>(edge, node1, node2);
        tableEdge.put(node1, node2, edgeWithVertices);
        if (undirected) {
            tableEdge.put(node2, node1, edgeWithVertices);
        }
    }

    public int numVertices() {
        return setVertices.size();
    }

    public int numEdges() {
        return tableEdge.size() / 2;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("<graph>");
        sb.append(System.lineSeparator());

        for (V vertice : setVertices) {

            sb.append("Vertice: ");
            sb.append(vertice);
            sb.append("\t");

            appendEdges(sb, vertice);

            sb.append(System.lineSeparator());
        }

        sb.append("</graph>");
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    private void appendEdges(StringBuilder sb, V vertice) {
        boolean first = true;
        for (EdgeTypeWithVertices<V, E> edgeWith : edgesOf(vertice)) {

            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            sb.append(edgeWith.getEdge());
        }
    }
}
