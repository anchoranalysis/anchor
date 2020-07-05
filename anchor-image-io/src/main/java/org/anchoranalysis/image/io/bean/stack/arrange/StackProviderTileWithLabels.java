package org.anchoranalysis.image.io.bean.stack.arrange;

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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterOverlay;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterTile;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProviderArrangeRaster;
import org.anchoranalysis.image.io.bean.stack.StackProviderGenerateString;
import org.anchoranalysis.image.io.generator.raster.StringRasterGenerator;
import org.anchoranalysis.image.stack.Stack;

// A short-cut provider for tiling a number of stack providers with labels
public class StackProviderTileWithLabels extends StackProvider {

	// START BEAN PROPERTIES
	@BeanField
	private List<StackProviderWithLabel> list = new ArrayList<>();
	
	@BeanField
	private int numCols = 3;
	
	@BeanField
	boolean createShort;
	
	@BeanField
	boolean scaleLabel = true;
	
	@BeanField
	boolean expandLabelZ = false;		// Repeats the label in the z-dimension to match the stackProvider
	// END BEAN PROPERTIES
		
	public static StackProviderArrangeRaster createStackProvider(
		List<StackProviderWithLabel> list,
		int numCols,
		boolean createShort,
		boolean scaleLabel,
		boolean expandLabelZ
	) {
		
		StackProviderArrangeRaster spar = new StackProviderArrangeRaster();
		spar.setCreateShort(createShort);
		spar.setForceRGB(true);	// Makes everything an RGB output
		spar.setList( new ArrayList<StackProvider>() );
		
		// Add stack providers
		for( StackProviderWithLabel spwl : list ) {
			spar.getList().add( spwl.getStackProvider() );
			spar.getList().add(
				addGenerateString(spwl, createShort, scaleLabel, expandLabelZ)
			);
		}
		
		ArrangeRasterTile art = new ArrangeRasterTile();
		art.setNumCols( numCols );
		art.setNumRows( (int) Math.ceil( ((double) list.size()) / numCols) );
		
		ArrangeRasterOverlay arOverlay = new ArrangeRasterOverlay();
		arOverlay.setHorizontalAlign("left");
		arOverlay.setVerticalAlign("top");
		arOverlay.setzAlign("repeat");
		
		art.setCellDefault(arOverlay);
		
		spar.setArrangeRaster(art);
		
		return spar;
	}
	
	private static StackProviderGenerateString addGenerateString(
		StackProviderWithLabel spwl,
		boolean createShort,
		boolean scaleLabel,
		boolean expandLabelZ
	) {
		StackProviderGenerateString spgs = new StackProviderGenerateString();
		
		StringRasterGenerator srg = new StringRasterGenerator( spwl.getLabel() );
		srg.setText( spwl.getLabel() );
		srg.setWidth(-1);
		srg.setHeight(-1);
		srg.setPadding(3);
		
		spgs.setStringRasterGenerator(srg);
		spgs.setCreateShort(createShort);
		if (scaleLabel) {
			spgs.setInstensityProvider( spwl.getStackProvider() );
		}
		if (expandLabelZ) {
			spgs.setRepeatZProvider( spwl.getStackProvider() );
		}
		return spgs;
	}

	@Override
	public Stack create() throws CreateException {
		StackProviderArrangeRaster arrangeRaster = createStackProvider(list, numCols, createShort, scaleLabel, expandLabelZ);
		try {
			arrangeRaster.initRecursive(getSharedObjects(), getLogger() );
		} catch (InitException e) {
			throw new CreateException(e);
		}
		return arrangeRaster.create();
	}

	public List<StackProviderWithLabel> getList() {
		return list;
	}

	public void setList(List<StackProviderWithLabel> list) {
		this.list = list;
	}

	public int getNumCols() {
		return numCols;
	}

	public void setNumCols(int numCols) {
		this.numCols = numCols;
	}

	public void setCreateShort(boolean createShort) {
		this.createShort = createShort;
	}

	public boolean isCreateShort() {
		return createShort;
	}

	public boolean isScaleLabel() {
		return scaleLabel;
	}

	public void setScaleLabel(boolean scaleLabel) {
		this.scaleLabel = scaleLabel;
	}

	public boolean isExpandLabelZ() {
		return expandLabelZ;
	}

	public void setExpandLabelZ(boolean expandLabelZ) {
		this.expandLabelZ = expandLabelZ;
	}

	
}
