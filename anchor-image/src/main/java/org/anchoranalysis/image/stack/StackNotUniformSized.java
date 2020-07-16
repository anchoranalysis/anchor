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
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public class StackNotUniformSized implements Iterable<Channel> {

    // We store our values in an arraylist of channels
    private final List<Channel> chnls;

    // Image stack
    public StackNotUniformSized() {
        chnls = new ArrayList<>();
    }

    // Create a stack from a channel
    public StackNotUniformSized(Channel chnl) {
        this();
        addChnl(chnl);
    }

    public StackNotUniformSized extractSlice(int z) {

        StackNotUniformSized stackOut = new StackNotUniformSized();
        for (int c = 0; c < chnls.size(); c++) {
            stackOut.addChnl(chnls.get(c).extractSlice(z));
        }
        return stackOut;
    }

    // TODO MAKE MORE EFFICIENT
    public StackNotUniformSized maxIntensityProj() {

        StackNotUniformSized stackOut = new StackNotUniformSized();
        for (int c = 0; c < chnls.size(); c++) {
            // TODO make more efficient than duplicateByte()
            stackOut.addChnl(chnls.get(c).duplicate().maxIntensityProjection());
        }
        return stackOut;
    }

    public void clear() {
        chnls.clear();
    }

    public final void addChnl(Channel chnl) {
        chnls.add(chnl);
    }

    public final Channel getChnl(int index) {
        return chnls.get(index);
    }

    public final int getNumChnl() {
        return chnls.size();
    }

    public ImageDimensions getFirstDimensions() {
        assert (getNumChnl() > 0);
        return chnls.get(0).getDimensions();
    }

    public boolean isUniformSized() {

        if (chnls.size() <= 1) {
            return true;
        }

        ImageDimensions sd = chnls.get(0).getDimensions();

        for (int c = 1; c < chnls.size(); c++) {

            if (!sd.equals(chnls.get(c).getDimensions())) {
                return false;
            }
        }

        return true;
    }

    public boolean isUniformTyped() {

        if (chnls.size() <= 1) {
            return true;
        }

        VoxelDataType dataType = chnls.get(0).getVoxelDataType();

        for (int c = 1; c < chnls.size(); c++) {

            if (!dataType.equals(chnls.get(c).getVoxelDataType())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Iterator<Channel> iterator() {
        return chnls.iterator();
    }

    public StackNotUniformSized duplicate() {
        StackNotUniformSized out = new StackNotUniformSized();
        for (Channel chnl : this) {
            out.addChnl(chnl.duplicate());
        }
        return out;
    }
}
