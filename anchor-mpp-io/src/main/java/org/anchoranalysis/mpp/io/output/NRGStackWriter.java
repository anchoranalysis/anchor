package org.anchoranalysis.mpp.io.output;

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.io.generator.raster.ChnlGenerator;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceUtilities;
import org.anchoranalysis.io.generator.serialized.KeyValueParamsGenerator;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class NRGStackWriter {

	private NRGStackWriter() {
	}
	
	public static void writeNRGStack( NRGStackWithParams nrgStack,	BoundOutputManagerRouteErrors outputManager, LogErrorReporter logErrorReporter ) {
		// We write the nrg stack seperately as individual channels
		GeneratorSequenceUtilities.generateListAsSubfolder(
			"nrgStack",
			2,
			nrgStack.getNrgStack().asStack().asListChnls(),
			new ChnlGenerator("nrgStackChnl"),
			outputManager,
			logErrorReporter.getErrorReporter()
		);
		
		if (nrgStack.getParams()!=null) {

			//XStreamGenerator<NRGElemParamsFromImage> generatorParamsImage = new XStreamGenerator<NRGElemParamsFromImage>(nrgStack.getParams(),"nrgStackImageParams");
			outputManager.getWriterCheckIfAllowed().write(
				"nrgStackParams",
				() ->  new KeyValueParamsGenerator(nrgStack.getParams(), "nrgStackParams")
			);
		}
	}
}
