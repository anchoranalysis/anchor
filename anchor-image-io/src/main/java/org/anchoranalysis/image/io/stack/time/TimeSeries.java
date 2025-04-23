/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.io.stack.time;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.anchoranalysis.image.core.channel.Channel; // NOSONAR
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * An ordered collection of {@link Stack}s, each representing a frame in a time-series.
 *
 * @author Owen Feehan
 */
public class TimeSeries implements Iterable<Stack> {

    private final List<Stack> list;

    /**
     * Creates a {@link TimeSeries} containing a single frame only.
     *
     * @param frame the single frame.
     */
    public TimeSeries(Stack frame) {
        this.list = Arrays.asList(frame);
    }

    /**
     * Creates a {@link TimeSeries} from a stream of frames.
     *
     * @param frames the frames.
     */
    public TimeSeries(Stream<Stack> frames) {
        this.list = frames.toList();
    }

    /**
     * Gets a frame in the time-series.
     *
     * @param index the index of the frame, beginning at 0.
     * @return the corresponding frame.
     * @throws IndexOutOfBoundsException if no frame exists at {@code index}.
     */
    public Stack getFrame(int index) {
        return list.get(index);
    }

    /**
     * Whether the series contains no frames?
     *
     * @return true if no frames exist in the series. false if at least one frame exists.
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * The number of frames in the time-series.
     *
     * @return the number of frames.
     */
    public int size() {
        return list.size();
    }

    /**
     * Whether all {@link Channel}s in all frames in the times-series have a particular voxel-data
     * type?
     *
     * @param voxelDataType the voxel data-type to match.
     * @return true iff all frames have this data-type.
     */
    public boolean allChannelsHaveType(VoxelDataType voxelDataType) {
        return list.stream().allMatch(stack -> stack.allChannelsHaveType(voxelDataType));
    }

    @Override
    public Iterator<Stack> iterator() {
        return list.iterator();
    }
}
