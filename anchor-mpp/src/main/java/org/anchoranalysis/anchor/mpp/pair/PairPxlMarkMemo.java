/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pair;

import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;

public class PairPxlMarkMemo {

    private final VoxelizedMarkMemo source;
    private final VoxelizedMarkMemo destination;

    public PairPxlMarkMemo(VoxelizedMarkMemo source, VoxelizedMarkMemo destination) {
        super();

        if (source.getMark().getId() < destination.getMark().getId()) {
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
        if (!(othero instanceof PairPxlMarkMemo)) {
            return false;
        }

        PairPxlMarkMemo other = (PairPxlMarkMemo) othero;
        return ((this.source.equals(other.source)) && (this.destination.equals(other.destination)));
    }

    @Override
    public int hashCode() {
        return (source.getMark().getId() * 3) + destination.getMark().getId();
    }

    public VoxelizedMarkMemo getSource() {
        return source;
    }

    public VoxelizedMarkMemo getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return String.format("%d--%d", source.getMark().getId(), destination.getMark().getId());
    }
}
