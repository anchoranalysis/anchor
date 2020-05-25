package org.anchoranalysis.image.bean.arrangeraster;

/*
 * #%L
 * anchor-image-io
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.annotation.Positive;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.arrangeraster.ArrangeRasterException;
import org.anchoranalysis.image.arrangeraster.BBoxSetOnPlane;
import org.anchoranalysis.image.arrangeraster.IArrangeRaster;
import org.anchoranalysis.image.arrangeraster.TableItemArrangement;
import org.anchoranalysis.image.arrangeraster.TableItemException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class ArrangeRasterTile extends ArrangeRasterBean {

	// START BEAN PROPERTIES
	@BeanField @Positive
	private int numRows = -1;
	
	@BeanField @Positive
	private int numCols = -1;
	
	@BeanField @OptionalBean
	private List<ArrangeRasterCell> cells = new ArrayList<>();
	
	@BeanField
	private ArrangeRasterBean cellDefault = new SingleRaster();
	// END BEAN PROPERTIES

	private class TableCreator implements TableItemArrangement.ITableCreator<BBoxSetOnPlane> {
		
		private Iterator<RGBStack> rasterIterator;
		
		public TableCreator(Iterator<RGBStack> rasterIterator) {
			super();
			this.rasterIterator = rasterIterator;
		}

		
		
		
		// We can make this more efficient by using a lookup table for the cells
		// But as there should be relatively few exceptions, we just always loop
		//   through the list
		private IArrangeRaster createArrangeRasterForItem(int rowPos, int colPos) {
		
			if (cells!=null) {
				for (ArrangeRasterCell cell : cells) {
					if (cell.getRow()==rowPos && cell.getCol()==colPos) {
						assert(cell.getArrangeRaster()!=null);
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
		public BBoxSetOnPlane createNext( int rowPos, int colPos) throws TableItemException {
			try {
				return createArrangeRasterForItem(rowPos, colPos).createBBoxSetOnPlane(rasterIterator);
			} catch (ArrangeRasterException e) {
				throw new TableItemException(e);
			}
		}
	}

	private static void addShifted( Iterable<BoundingBox> src, BBoxSetOnPlane dest, int shiftX, int shiftY ) {

		// We now loop through each item in the cell, and add to our output set with
		//   the correct offset
		for (BoundingBox bbox : src) {

			Point3i crnrMin = new Point3i( bbox.getCrnrMin() );
			crnrMin.incrX(shiftX);
			crnrMin.incrY(shiftY);

			dest.add(
				new BoundingBox(crnrMin, bbox.extnt())
			);
		}

	}
	
	private static BBoxSetOnPlane createSet( TableItemArrangement<BBoxSetOnPlane> table, MaxWidthHeight maxWidthHeight ) {
		
		BBoxSetOnPlane set = new BBoxSetOnPlane();
		
		// We iterator over every cell in the table
		for (int rowPos=0; rowPos<table.getNumRowsUsed(); rowPos++) {
			for (int colPos=0; colPos<table.getNumColsUsed(); colPos++) {
		
				if (!table.isCellUsed(rowPos,colPos)) {
					break;
				}
				
				BBoxSetOnPlane bboxSet = table.get(rowPos,colPos);
				
				int rowHeight = maxWidthHeight.getMaxHeightForRow( rowPos );
				int colWidth = maxWidthHeight.getMaxWidthForCol( colPos );
				
				int rowX = maxWidthHeight.sumWidthBeforeCol(colPos);
				int rowY = maxWidthHeight.sumHeightBeforeRow(rowPos);; 
				
				int x = rowX + ((colWidth - bboxSet.getExtnt().getX()) / 2);	// We center
				int y = rowY + ((rowHeight - bboxSet.getExtnt().getY()) / 2);	// We center
				
				addShifted( bboxSet, set, x, y );
			}
		}
		
		set.setExtnt(
			new Extent(
				maxWidthHeight.getTotalWidth(),
				maxWidthHeight.getTotalHeight(),
				maxWidthHeight.getMaxZ()
			)	
		);
		
		return set;
	}
	
	
	@Override
	public BBoxSetOnPlane createBBoxSetOnPlane( final Iterator<RGBStack> rasterIterator ) throws ArrangeRasterException {
	
		try {
			TableItemArrangement<BBoxSetOnPlane> table = new TableItemArrangement<>( new TableCreator(rasterIterator), numRows, numCols );
			
			MaxWidthHeight maxWidthHeight = new MaxWidthHeight(table, numRows, numCols);
			
			return createSet(table, maxWidthHeight );
			
		} catch (TableItemException e) {
			throw new ArrangeRasterException(e);
		}
	}

	public int getNumRows() {
		return numRows;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	public int getNumCols() {
		return numCols;
	}

	public void setNumCols(int numCols) {
		this.numCols = numCols;
	}

	public List<ArrangeRasterCell> getCells() {
		return cells;
	}

	public void setCells(List<ArrangeRasterCell> cells) {
		this.cells = cells;
	}

	public ArrangeRasterBean getCellDefault() {
		return cellDefault;
	}

	public void setCellDefault(ArrangeRasterBean cellDefault) {
		this.cellDefault = cellDefault;
	}

}
