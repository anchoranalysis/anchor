/* (C)2020 */
package org.anchoranalysis.image.bean.arrangeraster;

import java.util.List;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.BBoxSetOnPlane;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.TableItemArrangement;

class MaxWidthHeight {

    private int[] rowMaxHeight;
    private int[] colMaxWidth;
    private int totalHeight;
    private int totalWidth;
    private int maxZ;

    public MaxWidthHeight(TableItemArrangement<BBoxSetOnPlane> firstGeneratedTable) {

        // We calculate a max width for every column
        rowMaxHeight = new int[firstGeneratedTable.getNumRowsUsed()];
        colMaxWidth = new int[firstGeneratedTable.getNumColsUsed()];
        totalHeight = 0;
        totalWidth = 0;
        maxZ = 0;

        for (int i = 0; i < firstGeneratedTable.getNumRowsUsed(); i++) {
            List<BBoxSetOnPlane> rowList = firstGeneratedTable.getRow(i);
            int height = getMaxHeightFromList(rowList);
            rowMaxHeight[i] = height;
            totalHeight += height;
            maxZ = Math.max(maxZ, getMaxZFromList(rowList));
        }

        for (int i = 0; i < firstGeneratedTable.getNumColsUsed(); i++) {
            List<BBoxSetOnPlane> colList = firstGeneratedTable.getCol(i);
            int width = getMaxWidthFromList(colList);
            colMaxWidth[i] = width;
            totalWidth += width;
            maxZ = Math.max(maxZ, getMaxZFromList(colList));
        }
    }

    public int sumHeightBeforeRow(int index) {
        int cum = 0;
        for (int i = 0; i < index; i++) {
            cum += rowMaxHeight[i];
        }
        return cum;
    }

    public int sumWidthBeforeCol(int index) {
        int cum = 0;
        for (int i = 0; i < index; i++) {
            cum += colMaxWidth[i];
        }
        return cum;
    }

    public int getMaxHeightForRow(int index) {
        return rowMaxHeight[index];
    }

    public int getMaxWidthForCol(int index) {
        return colMaxWidth[index];
    }

    public int getTotalWidth() {
        return totalWidth;
    }

    public int getTotalHeight() {
        return totalHeight;
    }

    private static int getMaxWidthFromList(List<BBoxSetOnPlane> list) {
        // Assumes dim are the same for all channels

        int max = -1;

        for (BBoxSetOnPlane item : list) {
            max = Math.max(max, item.getExtent().getX());
        }

        return max;
    }

    private static int getMaxHeightFromList(List<BBoxSetOnPlane> list) {
        // Assumes dim are the same for all channels

        int max = -1;

        for (BBoxSetOnPlane item : list) {
            assert (item != null);
            max = Math.max(max, item.getExtent().getY());
        }

        return max;
    }

    private static int getMaxZFromList(List<BBoxSetOnPlane> list) {
        // Assumes dim are the same for all channels

        int max = -1;

        for (BBoxSetOnPlane item : list) {
            assert (item != null);
            max = Math.max(max, item.getExtent().getZ());
        }

        return max;
    }

    public int getMaxZ() {
        return maxZ;
    }
}
