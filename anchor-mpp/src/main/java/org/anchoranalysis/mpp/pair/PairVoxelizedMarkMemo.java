/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.pair;

import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

/**
 * Represents a pair of VoxelizedMarkMemo objects, with a defined source and destination.
 *
 * <p>The pair is ordered such that the source always has a lower identifier than the destination.
 */
public class PairVoxelizedMarkMemo {

    private final VoxelizedMarkMemo source;
    private final VoxelizedMarkMemo destination;

    /**
     * Constructs a new PairVoxelizedMarkMemo.
     *
     * @param source the first VoxelizedMarkMemo
     * @param destination the second VoxelizedMarkMemo
     */
    public PairVoxelizedMarkMemo(VoxelizedMarkMemo source, VoxelizedMarkMemo destination) {
        super();

        if (source.getMark().getIdentifier() < destination.getMark().getIdentifier()) {
            this.source = source;
            this.destination = destination;
        } else {
            this.destination = source;
            this.source = destination;
        }
    }

    @Override
    public boolean equals(Object othero) {

        if (othero == null) {
            return false;
        }
        if (othero == this) {
            return true;
        }
        if (!(othero instanceof PairVoxelizedMarkMemo)) {
            return false;
        }

        PairVoxelizedMarkMemo other = (PairVoxelizedMarkMemo) othero;
        return ((this.source.equals(other.source)) && (this.destination.equals(other.destination)));
    }

    @Override
    public int hashCode() {
        return (source.getMark().getIdentifier() * 3) + destination.getMark().getIdentifier();
    }

    /**
     * Gets the source VoxelizedMarkMemo.
     *
     * @return the source VoxelizedMarkMemo
     */
    public VoxelizedMarkMemo getSource() {
        return source;
    }

    /**
     * Gets the destination VoxelizedMarkMemo.
     *
     * @return the destination VoxelizedMarkMemo
     */
    public VoxelizedMarkMemo getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return String.format(
                "%d--%d", source.getMark().getIdentifier(), destination.getMark().getIdentifier());
    }
}
