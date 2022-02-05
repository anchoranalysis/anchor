/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.channel.input;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.anchoranalysis.image.core.channel.Channel; // NOSONAR
import org.anchoranalysis.image.io.bean.channel.IndexedChannel;

/**
 * A mapping of assigned channel-names to particular indices.
 *
 * <p>This can be useful for opening an image file that contains many {@link Channel}s and mapping
 * all, or some subset, of the channels to semantically-meaningful identifiers.
 *
 * <p>The class preserves insertion order (via {@link #add} by internally using a {@link
 * LinkedHashMap}.
 *
 * @author Owen Feehan
 */
public class ChannelMap {

    /** The underlying map from user-assigned name to {@link IndexedChannel}. */
    private Map<String, IndexedChannel> map = new LinkedHashMap<>();

    /**
     * Adds a {@link IndexedChannel} into the map.
     *
     * @param channel the channel to add.
     */
    public void add(IndexedChannel channel) {
        map.put(channel.getName(), channel);
    }

    /**
     * Gets the index corresponding to a particular channel-name.
     *
     * @param name the channel name to find a corresponding index for.
     * @return the index (beginning at 0) for the channel-name, or -1 if no such name exists in the
     *     map.
     */
    public int get(String name) {
        IndexedChannel entry = map.get(name);
        if (entry != null) {
            return entry.getIndex();
        } else {
            return -1;
        }
    }

    /**
     * Like {@link #get} but throws an exception if a particular channel-name does not exist in the
     * map.
     *
     * @param name the channel name to find a corresponding index for.
     * @return the index (beginning at 0) for the channel-name.
     * @throws IndexOutOfBoundsException if the channel doesn't exist in the map.
     */
    public int getException(String name) {
        int ind = get(name);
        if (ind != -1) {
            return ind;
        } else {
            throw new IndexOutOfBoundsException(
                    String.format("No channel index for '%s' in map", name));
        }
    }

    /**
     * The names of the {@link Channel}s in the map.
     *
     * @return a {@link Set} view on the names of the {@link Channel}s in the map.
     */
    public Set<String> names() {
        return map.keySet();
    }

    /**
     * All {@link IndexedChannel}s that exist in the map.
     *
     * @return a view on the {@link IndexedChannel}s in the map.
     */
    public Collection<IndexedChannel> values() {
        return map.values();
    }
}
