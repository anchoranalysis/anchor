/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pxlmark;

import java.util.List;
import java.util.function.IntConsumer;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.pixelpart.IndexByChnl;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.histogram.Histogram;

class AddPxlsToHistogram {

    private AddPxlsToHistogram() {}

    public static void addPxls(
            byte membership,
            List<RegionMembershipWithFlags> listRegionMembership,
            IndexByChnl<Histogram> partitionList,
            BufferArrList bufferArrList,
            int globalOffset,
            int zLocal) {
        iterateRegions(
                membership,
                listRegionMembership,
                r -> addRegionToPartition(partitionList, bufferArrList, globalOffset, r, zLocal));
    }

    private static void addRegionToPartition(
            IndexByChnl<Histogram> partitionList,
            BufferArrList bufferArrList,
            int globalOffset,
            int r,
            int zLocal) {
        for (int i = 0; i < partitionList.size(); i++) {
            byte val = bufferArrList.get(i).get(globalOffset);
            partitionList.get(i).addToPxlList(r, zLocal, ByteConverter.unsignedByteToInt(val));
        }
    }

    private static void iterateRegions(
            byte membership,
            List<RegionMembershipWithFlags> listRegionMembership,
            IntConsumer func) {
        // We optimise this
        for (int r = 0; r < listRegionMembership.size(); r++) {
            RegionMembershipWithFlags rm = listRegionMembership.get(r);
            if (rm.isMemberFlag(membership)) {
                func.accept(r);
            }
        }
    }
}
