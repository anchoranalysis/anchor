package org.anchoranalysis.image.io.generator.raster.obj.collection;

import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;


/**
 * Base class for generators that accept a set of objects as input
 * 
 * @author owen
 *
 */
public abstract class ObjsGenerator extends RasterGenerator implements IterableGenerator<ObjMaskCollection> {
	
	private ObjMaskCollection objs;
	private ImageDim dim;
	
	public ObjsGenerator(ImageDim dim) {
		this.dim = dim;
	}
	
	public ObjsGenerator(ObjMaskCollection objs, ImageDim dim) {
		this(dim);
		this.objs = objs;
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("raster", "maskCollection");
	}

	@Override
	public boolean isRGB() {
		return false;
	}

	@Override
	public ObjMaskCollection getIterableElement() {
		return objs;
	}

	@Override
	public void setIterableElement(ObjMaskCollection element)
			throws SetOperationFailedException {
		this.objs = element;
	}

	@Override
	public void start() throws OutputWriteFailedException {
		
	}

	@Override
	public void end() throws OutputWriteFailedException {
		
	}

	@Override
	public Generator getGenerator() {
		return this;
	}

	public ImageDim getDimensions() {
		return dim;
	}

	protected ObjMaskCollection getObjs() {
		return objs;
	}
}
