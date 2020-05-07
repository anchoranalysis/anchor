package org.anchoranalysis.image.io.input;

import java.nio.file.Path;

import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.io.output.bound.BoundIOContext;

public class ImageInitParamsFactory {

	private ImageInitParamsFactory() {}
	
	public static ImageInitParams create( SharedObjects so, Path modelDir ) {
		return new ImageInitParams(so, modelDir);
	}
	
	public static ImageInitParams create( BoundIOContext context ) {
		SharedObjects so = new SharedObjects( context.getLogger() );
		return create(
			so,
			context.getModelDirectory()
		);
	}
}
