package ch.ethz.biol.cell.gui.image.provider;

/*
 * #%L
 * anchor-mpp-io
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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.region.RegionExtracter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

import ch.ethz.biol.cell.gui.overlay.BoundColoredOverlayCollection;
import ch.ethz.biol.cell.imageprocessing.io.RegionExtracterFromOverlay;

public class BoundOverlayedDisplayStack {

	private DisplayStack background;
	private BoundColoredOverlayCollection overlay;
	private RegionExtracter regionExtracter;
		
	public BoundOverlayedDisplayStack(DisplayStack background) {
		super();
		this.background = background;
	}

	public BoundOverlayedDisplayStack(DisplayStack background, BoundColoredOverlayCollection overlay) {
		super();
		this.background = background;
		this.overlay = overlay;
	}
	
	public void initRegionExtracter() {
		if (overlay==null) {
			this.regionExtracter = background.createRegionExtracter();	
		} else {
			RegionExtracter reBackground = background.createRegionExtracter();
			this.regionExtracter = new RegionExtracterFromOverlay(reBackground, overlay);
		}
	}

	public ImageDim getDimensions() {
		return background.getDimensions();
	}
	
	// Creates a new DisplayStack after imposing the overlay on the background
	public DisplayStack extractFullyOverlayed() throws OperationFailedException {
		RegionExtracter re = background.createRegionExtracter();
		return re.extractRegionFrom( new BoundingBox( background.getDimensions().getExtnt() ), 1.0 );
	}

	public final int getNumChnl() {
		return background.getNumChnl();
	}

	public VoxelDataType unconvertedDataType() {
		return background.unconvertedDataType();
	}

	public int getUnconvertedVoxelAt(int c, int x, int y, int z) {
		return background.getUnconvertedVoxelAt(c, x, y, z);
	}

	public RegionExtracter createRegionExtracter() {
		initRegionExtracter();
		return regionExtracter;
	}

	public DisplayStack getBackground() {
		return background;
	}
}
