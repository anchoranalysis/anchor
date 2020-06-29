package org.anchoranalysis.image.bean.nonbean.arrangeraster;

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
import java.util.List;

public class TableItemArrangement<ItemType> {

	private List<List<ItemType>> rows;
	private List<List<ItemType>> cols;
	
	private int numCols;
	
	public static interface ITableCreator<ItemType> {
		
		boolean hasNext();
		
		ItemType createNext( int rowPos, int colPos ) throws TableItemException;
	}
	
	
	// Assumes we will only ever call get() on an index that is one more than
	//  than existing items, so add() is sufficient
	private List<ItemType> getListOrAdd( List<List<ItemType>> list, int index ) {
		
		
		if (index >= list.size()) {
			List<ItemType> crntList = new ArrayList<>();
			list.add( crntList );
			return crntList;
		} else {
			return list.get(index);
		}
	}
	
	// Is the cell in use
	public boolean isCellUsed( int rowIndex, int colIndex ) {
		if (rowIndex >= rows.size()) {
			return false;
		}
		if (colIndex >= rows.get(rowIndex).size()) {
			return false;
		}
		return true;
	}
	
	public ItemType get( int rowIndex, int colIndex ) {
		return rows.get(rowIndex).get(colIndex);
	}
	
	public List<ItemType> getRow( int index ) {
		return rows.get(index);
	}
	
	public List<ItemType> getCol( int index ) {
		return cols.get(index);
	}
	
	public int getNumRowsUsed() {
		return rows.size();
	}
	
	public int getNumColsUsed() {
		return cols.size();
	}
	
	public int getRowForListIndex( int index ) {
		return index / numCols;
	}
	
	public int getColForListIndex( int index ) {
		return index % numCols;
	}
	
	public TableItemArrangement( ITableCreator<ItemType> rasterIterator, int numRows, int numCols ) throws TableItemException {
		
		this.numCols = numCols;
		
		rows = new ArrayList<>();
		cols = new ArrayList<>();
		
		for (int crntRow = 0; crntRow<numRows; crntRow++) {
			for (int crntCol = 0; crntCol<numCols; crntCol++) {
			
				if (!rasterIterator.hasNext()) {
					return;
				}
				
				ItemType item = rasterIterator.createNext(crntRow, crntCol);
				assert( item != null );
				
				List<ItemType> crntRowList = getListOrAdd( rows, crntRow );
				List<ItemType> crntColList = getListOrAdd( cols, crntCol );
				
				crntRowList.add(item);
				crntColList.add(item);
			}
		}
	}

}