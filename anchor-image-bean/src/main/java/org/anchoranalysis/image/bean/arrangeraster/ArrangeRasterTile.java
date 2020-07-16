/* (C)2020 */
package org.anchoranalysis.image.bean.arrangeraster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.annotation.Positive;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.ArrangeRaster;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.ArrangeRasterException;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.BBoxSetOnPlane;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.TableItemArrangement;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.TableItemException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class ArrangeRasterTile extends ArrangeRasterBean {

    // START BEAN PROPERTIES
    @BeanField @Positive @Getter @Setter private int numRows = -1;

    @BeanField @Positive @Getter @Setter private int numCols = -1;

    @BeanField @OptionalBean @Getter @Setter
    private List<ArrangeRasterCell> cells = new ArrayList<>();

    @BeanField @Getter @Setter private ArrangeRasterBean cellDefault = new SingleRaster();
    // END BEAN PROPERTIES

    private class CreateTable implements TableItemArrangement.TableCreator<BBoxSetOnPlane> {

        private Iterator<RGBStack> rasterIterator;

        public CreateTable(Iterator<RGBStack> rasterIterator) {
            super();
            this.rasterIterator = rasterIterator;
        }

        // We can make this more efficient by using a lookup table for the cells
        // But as there should be relatively few exceptions, we just always loop
        //   through the list
        private ArrangeRaster createArrangeRasterForItem(int rowPos, int colPos) {

            if (cells != null) {
                for (ArrangeRasterCell cell : cells) {
                    if (cell.getRow() == rowPos && cell.getCol() == colPos) {
                        assert (cell.getArrangeRaster() != null);
                        return cell.getArrangeRaster();
                    }
                }
            }

            // If there's no explicit cell definition
            return cellDefault;
        }

        @Override
        public boolean hasNext() {
            return rasterIterator.hasNext();
        }

        @Override
        public BBoxSetOnPlane createNext(int rowPos, int colPos) throws TableItemException {
            try {
                return createArrangeRasterForItem(rowPos, colPos)
                        .createBBoxSetOnPlane(rasterIterator);
            } catch (ArrangeRasterException e) {
                throw new TableItemException(e);
            }
        }
    }

    private static void addShifted(
            Iterable<BoundingBox> src, BBoxSetOnPlane dest, int shiftX, int shiftY) {

        // We now loop through each item in the cell, and add to our output set with
        //   the correct offset
        for (BoundingBox bbox : src) {

            Point3i cornerMin = new Point3i(bbox.cornerMin());
            cornerMin.incrementX(shiftX);
            cornerMin.incrementY(shiftY);

            dest.add(new BoundingBox(cornerMin, bbox.extent()));
        }
    }

    private static BBoxSetOnPlane createSet(
            TableItemArrangement<BBoxSetOnPlane> table, MaxWidthHeight maxWidthHeight) {

        BBoxSetOnPlane set =
                new BBoxSetOnPlane(
                        new Extent(
                                maxWidthHeight.getTotalWidth(),
                                maxWidthHeight.getTotalHeight(),
                                maxWidthHeight.getMaxZ()));

        // We iterator over every cell in the table
        for (int rowPos = 0; rowPos < table.getNumRowsUsed(); rowPos++) {
            for (int colPos = 0; colPos < table.getNumColsUsed(); colPos++) {

                if (!table.isCellUsed(rowPos, colPos)) {
                    break;
                }

                BBoxSetOnPlane bboxSet = table.get(rowPos, colPos);

                int rowHeight = maxWidthHeight.getMaxHeightForRow(rowPos);
                int colWidth = maxWidthHeight.getMaxWidthForCol(colPos);

                int rowX = maxWidthHeight.sumWidthBeforeCol(colPos);
                int rowY = maxWidthHeight.sumHeightBeforeRow(rowPos);

                int x = rowX + ((colWidth - bboxSet.getExtent().getX()) / 2); // We center
                int y = rowY + ((rowHeight - bboxSet.getExtent().getY()) / 2); // We center

                addShifted(bboxSet, set, x, y);
            }
        }
        return set;
    }

    @Override
    public BBoxSetOnPlane createBBoxSetOnPlane(final Iterator<RGBStack> rasterIterator)
            throws ArrangeRasterException {

        try {
            TableItemArrangement<BBoxSetOnPlane> table =
                    new TableItemArrangement<>(new CreateTable(rasterIterator), numRows, numCols);

            MaxWidthHeight maxWidthHeight = new MaxWidthHeight(table);

            return createSet(table, maxWidthHeight);

        } catch (TableItemException e) {
            throw new ArrangeRasterException(e);
        }
    }
}
