package org.anchoranalysis.mpp.io.cfg.generator;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.io.cfg.ColoredCfgWithDisplayStack;

public abstract class CfgGeneratorBase extends RasterGenerator implements IterableObjectGenerator<ColoredCfgWithDisplayStack,Stack> {

	private String manifestDescriptionFunction = "cfg";
	
	private OverlayWriter writer;
	private ColoredCfgWithDisplayStack cws;
	private IDGetter<Overlay> idGetter;
	private RegionMembershipWithFlags regionMembership;
	
	public CfgGeneratorBase(OverlayWriter writer, ColoredCfgWithDisplayStack cws, IDGetter<Overlay> idGetter, RegionMembershipWithFlags regionMembership ) {
		super();
		this.writer = writer;
		this.cws = cws;
		this.idGetter = idGetter;
		this.regionMembership = regionMembership;
		this.setIterableElement(cws);
	}
	
	@Override
	public Stack generate() throws OutputWriteFailedException {
		try {
			RGBStack stack = ConvertDisplayStackToRGB.convert(
				background(cws.getStack())
			);
			
			ColoredOverlayCollection oc = OverlayCollectionMarkFactory.createColor(
				cws.getCfg(),
				regionMembership
			);
			
			writer.writeOverlays( oc, stack, idGetter );
			
			return stack.asStack();
			
		} catch (OperationFailedException e) {
			throw new OutputWriteFailedException(e);
		}
	}
	
	protected abstract DisplayStack background(DisplayStack stack) throws OperationFailedException;
	
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
	public boolean isRGB() {
		return true;
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
