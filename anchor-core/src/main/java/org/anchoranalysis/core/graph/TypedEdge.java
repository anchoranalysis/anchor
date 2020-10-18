package org.anchoranalysis.core.graph;

import lombok.AllArgsConstructor;
import lombok.Value;

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

/**
 * An edge that exists in a graph, together with the two vertices that it conntects.
 *
 * @author Owen Feehan
 * @param <V> vertex-type
 * @param <P> edge payload-type
 */
@Value
@AllArgsConstructor
public final class TypedEdge<V, P> {

    /** The edge. */
    private P payload;

    /** The vertex the edge joins <i>from</i>. */
    private V from;

    /** The vertex the edge joins <i>to</i>. */
    private V to;

    /**
     * The other vertex on the edge
     *
     * <p>i.e. the one that isn't {@code vertex}.
     *
     * @param vertex a vertex that should be one of the two vertices in the edge (but this is not
     *     checked).
     * @return the other vertex in the edge that isn't {@code vertex}.
     */
    public V otherVertex(V vertex) {
        if (from == vertex) {
            return to;
        } else {
            return from;
        }
    }

    /**
     * Describes the edge by showing the <i>to</i> vertex, and optionally the payload.
     *
     * @param includePayload iff true, the payload is included in the description
     * @return a string describing the edge
     */
    public String describeTo(boolean includePayload) {
        if (includePayload) {
            return String.format("%s (%s)", to, payload);
        } else {
            return to.toString();
        }
    }
}
