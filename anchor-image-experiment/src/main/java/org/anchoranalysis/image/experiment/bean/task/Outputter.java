package org.anchoranalysis.image.experiment.bean.task;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.io.generator.raster.ChnlGenerator;
import org.anchoranalysis.image.io.generator.raster.obj.ChnlMaskedWithObjGenerator;
import org.anchoranalysis.image.io.generator.raster.obj.ObjAsBinaryChnlGenerator;
import org.anchoranalysis.image.io.generator.raster.obj.rgb.RGBObjMaskGenerator;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithPropertiesCollection;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.io.bean.objmask.writer.RGBOutlineWriter;
import org.anchoranalysis.io.generator.collection.IterableGeneratorWriter;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

class Outputter {
	
	private Outputter() {
		
	}
	
	public static void writeOriginal( BoundOutputManagerRouteErrors outputManager, Chnl chnl, String outputName ) {
		outputManager.getWriterCheckIfAllowed().write(
			outputName,
			() -> new ChnlGenerator(chnl,"original")
		);
	}

	public static void writeMaskOutputs( ObjMaskCollection objs, Chnl chnl, BoundOutputManagerRouteErrors outputManager ) {
		writeMaskChnlAsSubfolder(objs, chnl, outputManager);
		writeMasksAsSubfolder(objs, chnl, outputManager);
		writeOutline(objs, chnl, outputManager);		
	}
	
	private static void writeMaskChnlAsSubfolder( ObjMaskCollection objs, Chnl chnl, BoundOutputManagerRouteErrors outputManager ) {
		// Write out the results as a subfolder
		IterableGeneratorWriter.writeSubfolder(
			outputManager,
			"maskChnl",
			"maskChnl",
			() -> new ChnlMaskedWithObjGenerator(chnl),
			objs.asList(),
			true
		);
	}
	
	private static void writeMasksAsSubfolder( ObjMaskCollection objs, Chnl chnl, BoundOutputManagerRouteErrors outputManager ) {
		// Write out the results as a subfolder
		IterableGeneratorWriter.writeSubfolder(
			outputManager,
			"mask",
			"mask",
			() -> new ObjAsBinaryChnlGenerator(255, chnl.getDimensions().getRes() ),
			objs.asList(),
			true
		);	
	}
	
	private static void writeOutline( ObjMaskCollection objs, Chnl chnl, BoundOutputManagerRouteErrors outputManager ) {
		outputManager.getWriterCheckIfAllowed().write(
			"outline",
			() -> {
				try {
					return new RGBObjMaskGenerator(
						new RGBOutlineWriter(),
						new ObjMaskWithPropertiesCollection(objs),
						DisplayStack.create(chnl),
						outputManager.getOutputWriteSettings().genDefaultColorIndex(objs.size())
					);
				} catch (CreateException | OperationFailedException e) {
					throw new ExecuteException(e);
				}
			}
		);
	}
}
