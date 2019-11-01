package ch.ethz.biol.cell.imageprocessing.io.generator.raster;

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
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.bean.objmask.writer.MIPWriter;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.OutputWriteFailedException;

import overlay.OverlayCollectionMarkFactory;
import ch.ethz.biol.cell.gui.overlay.Overlay;
import ch.ethz.biol.cell.gui.overlay.ColoredOverlayCollection;
import ch.ethz.biol.cell.imageprocessing.io.objmask.ObjMaskWriter;
import ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter.SimpleOverlayWriter;
import ch.ethz.biol.cell.mpp.mark.GlobalRegionIdentifiers;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMapSingleton;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

public class CfgMIPGenerator extends RasterGenerator implements IterableObjectGenerator<ColoredCfgWithDisplayStack,Stack> {

	private ObjMaskWriter maskWriter;
	private ColoredCfgWithDisplayStack cws;
	private IDGetter<Overlay> idGetter;
	private RegionMembershipWithFlags regionMembership;

	
	// We cache the last background, and background MIP
	private DisplayStack cachedBackground;
	private DisplayStack cachedBackgroundMIP;
	
	public CfgMIPGenerator(ObjMaskWriter maskWriter, IDGetter<Overlay> idGetter ) {
		this(maskWriter, null, idGetter, RegionMapSingleton.instance().membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE) );
	}
	
	public CfgMIPGenerator(
		ObjMaskWriter maskWriter,
		ColoredCfgWithDisplayStack cws,
		IDGetter<Overlay> idGetter,
		RegionMembershipWithFlags regionMembership
	) {
		super();
		this.maskWriter = new MIPWriter(maskWriter);
		this.idGetter = idGetter;
		this.setIterableElement(cws);
		this.regionMembership = regionMembership;
	}
	
	@Override
	public boolean isRGB() {
		return true;
	}
	
	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		// TODO we should be able to speed this up, by not repeatedly doing MIPs on the same background image, but using
		//  a cache, which save the previous calculation.
		
		if (cws.getStack()!=cachedBackground) {
			cachedBackground = cws.getStack();
			try {
				cachedBackgroundMIP = cws.getStack().maxIntensityProj();
			} catch (OperationFailedException e) {
				throw new OutputWriteFailedException(e);
			}
		}
		
		try {
			RGBStack backgroundMIPRGB = ConvertDisplayStackToRGB.convert(cachedBackgroundMIP);
			
			ColoredOverlayCollection oc = OverlayCollectionMarkFactory.createColor(
				cws.getCfg(),
				regionMembership
			);
			
			// Convert our displaystack into a RGB stack
			new SimpleOverlayWriter(maskWriter).writeOverlays(
				oc,
				backgroundMIPRGB,
				idGetter
			);
			return backgroundMIPRGB.asStack();
		} catch (OperationFailedException e) {
			throw new OutputWriteFailedException(e);
		}
	}

	@Override
	public ColoredCfgWithDisplayStack getIterableElement() {
		return this.cws;
	}

	@Override
	public void setIterableElement(ColoredCfgWithDisplayStack element) {
		this.cws = element;
	}

	@Override
	public ObjectGenerator<Stack> getGenerator() {
		return this;
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("raster", "cfg");
	}

	@Override
	public void start() throws OutputWriteFailedException {
	}


	@Override
	public void end() throws OutputWriteFailedException {
	}


}
