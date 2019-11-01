package ch.ethz.biol.cell.imageprocessing.io.generator.raster;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.bean.objmask.writer.ObjMaskWriter;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;

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
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.OutputWriteFailedException;

import overlay.OverlayCollectionMarkFactory;
import ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter.SimpleOverlayWriter;
import ch.ethz.biol.cell.mpp.mark.GlobalRegionIdentifiers;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMapSingleton;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

public class CfgGenerator extends RasterGenerator implements IterableObjectGenerator<ColoredCfgWithDisplayStack,Stack> {
	
	private OverlayWriter maskWriter;
	private ColoredCfgWithDisplayStack cws;

	private IDGetter<Overlay> idGetter;
	
	private String manifestDescriptionFunction = "cfg";
	
	private RegionMembershipWithFlags regionMembership;
	
	public CfgGenerator(ObjMaskWriter maskWriter, IDGetter<Overlay> idGetter ) {
		this(maskWriter, null, idGetter);
	}
	
	public CfgGenerator(ObjMaskWriter maskWriter, ColoredCfgWithDisplayStack cws, IDGetter<Overlay> idGetter ) {
		this( maskWriter, cws, idGetter, RegionMapSingleton.instance().membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE) );
	}
	
	public CfgGenerator(ObjMaskWriter maskWriter, ColoredCfgWithDisplayStack cws, IDGetter<Overlay> idGetter, RegionMembershipWithFlags regionMembership ) {
		super();
		this.maskWriter = new SimpleOverlayWriter(maskWriter);
		this.cws = cws;
		this.idGetter = idGetter;
		this.regionMembership = regionMembership;
	}

	@Override
	public boolean isRGB() {
		return true;
	}
	
	@Override
	public Stack generate() throws OutputWriteFailedException {
		try {
			RGBStack stack = ConvertDisplayStackToRGB.convert( cws.getStack() );
			
			ColoredOverlayCollection oc = OverlayCollectionMarkFactory.createColor(
				cws.getCfg(),
				regionMembership
			);
			
			maskWriter.writeOverlays( oc, stack, idGetter );
			
			return stack.asStack();
			
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
		return new ManifestDescription("raster", manifestDescriptionFunction);
	}

	@Override
	public void start() throws OutputWriteFailedException {
	}


	@Override
	public void end() throws OutputWriteFailedException {
	}

	public String getManifestDescriptionFunction() {
		return manifestDescriptionFunction;
	}

	public void setManifestDescriptionFunction(String manifestDescriptionFunction) {
		this.manifestDescriptionFunction = manifestDescriptionFunction;
	}

}
