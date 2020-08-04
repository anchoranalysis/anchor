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

package org.anchoranalysis.image.stack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public class StackNotUniformSized implements Iterable<Channel> {

    // We store our values in an arraylist of channels
    private final List<Channel> channels;

    // Image stack
    public StackNotUniformSized() {
        channels = new ArrayList<>();
    }
    
    // Image stack
    public StackNotUniformSized(Stream<Channel> channelStream) {
        channels = channelStream.collect( Collectors.toList() );
    }

    // Create a stack from a channel
    public StackNotUniformSized(Channel channel) {
        this();
        addChannel(channel);
    }

    public StackNotUniformSized extractSlice(int z) {

        StackNotUniformSized stackOut = new StackNotUniformSized();
        for (int c = 0; c < channels.size(); c++) {
            stackOut.addChannel(channels.get(c).extractSlice(z));
        }
        return stackOut;
    }

    public StackNotUniformSized maximumIntensityProjection() {

        StackNotUniformSized stackOut = new StackNotUniformSized();
        for (int c = 0; c < channels.size(); c++) {
            // TODO make more efficient than duplicate()
            stackOut.addChannel(channels.get(c).duplicate().maxIntensityProjection());
        }
        return stackOut;
    }

    public void clear() {
        channels.clear();
    }

    public final void addChannel(Channel chnl) {
        channels.add(chnl);
    }

    public final Channel getChannel(int index) {
        return channels.get(index);
    }

    public final int getNumberChannels() {
        return channels.size();
    }

    public ImageDimensions getFirstDimensions() {
        assert (getNumberChannels() > 0);
        return channels.get(0).dimensions();
    }

    public boolean isUniformlySized() {

        if (channels.size() <= 1) {
            return true;
        }

        ImageDimensions dimensions = channels.get(0).dimensions();

        for (int c = 1; c < channels.size(); c++) {

            if (!dimensions.equals(channels.get(c).dimensions())) {
                return false;
            }
        }

        return true;
    }

    public boolean isUniformTyped() {

        if (channels.size() <= 1) {
            return true;
        }

        VoxelDataType dataType = channels.get(0).getVoxelDataType();

        for (int c = 1; c < channels.size(); c++) {

            if (!dataType.equals(channels.get(c).getVoxelDataType())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Iterator<Channel> iterator() {
        return channels.iterator();
    }

    public StackNotUniformSized duplicate() {
        StackNotUniformSized out = new StackNotUniformSized();
        for (Channel channel : this) {
            out.addChannel(channel.duplicate());
        }
        return out;
    }
}
