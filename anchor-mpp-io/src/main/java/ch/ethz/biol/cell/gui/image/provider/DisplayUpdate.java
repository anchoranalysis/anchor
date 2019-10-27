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


import java.util.List;

import org.anchoranalysis.image.extent.BoundingBox;

/**
 * A command that:
 *   1. Updates parts of the display, while keeping an underlying OverlayedDisplayedStack the same
 *   2. Changes the OverlayedDisplayStack that is being shown
 * 
 * At least one of the two fields should be non-null. If both are non-null, then we change the displayStack
 *   while only refreshing particular parts of the image.
 * 
 * @author Owen Feehan
 *
 */
public class DisplayUpdate {

	/**
	 * What parts of the OverlayedDisplayStack to redraw. If null, everything is redrawn.
	 */
	private List<BoundingBox> redrawParts;
	
	/**
	 * A display-stack with some sort of overlay on top of it.
	 * If non-null, it indicates a new OverlayedDisplayStack has been assigned.
	 * If null, then the existing remains true.
	 */
	private BoundOverlayedDisplayStack displayStack;

	private DisplayUpdate(BoundOverlayedDisplayStack displayStack, List<BoundingBox> redrawParts) {
		super();
		this.redrawParts = redrawParts;
		this.displayStack = displayStack;
	}
	
	public static DisplayUpdate assignNewStack( BoundOverlayedDisplayStack displayStack ) {
		return new DisplayUpdate( displayStack, null );
	}
	
	public static DisplayUpdate redrawParts( List<BoundingBox> list ) {
		return new DisplayUpdate( null, list );
	}
	
	public static DisplayUpdate redrawEverything() {
		return new DisplayUpdate( null, null );
	}

	// Note that the bounding boxes are always in the original image co-ordinates, not the
	//   co-ordinates of the zoomed displays
	public List<BoundingBox> getRedrawParts() {
		return redrawParts;
	}

	public BoundOverlayedDisplayStack getDisplayStack() {
		return displayStack;
	}



	
}
