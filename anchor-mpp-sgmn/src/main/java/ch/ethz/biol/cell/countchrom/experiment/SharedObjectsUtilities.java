package ch.ethz.biol.cell.countchrom.experiment;

/*-
 * #%L
 * anchor-mpp-sgmn
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.io.generator.raster.ChnlGenerator;
import org.anchoranalysis.image.io.stack.StackCollectionOutputter;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceUtilities;
import org.anchoranalysis.io.generator.serialized.KeyValueParamsGenerator;
import org.anchoranalysis.io.output.OutputWriteFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

import ch.ethz.biol.cell.beaninitparams.MPPInitParams;
import ch.ethz.biol.cell.mpp.proposer.CombineDiverseProvidersAsStacks;

public class SharedObjectsUtilities {
	
	// TODO make this more elegant in the design
	// We make a special exception for writing our nrgStacks
	public static void writeNRGStackParams( ImageInitParams soImage, String nrgParamsName, BoundOutputManagerRouteErrors outputManager, LogErrorReporter logErrorReporter ) {
		
		try {
			if (soImage.getStackCollection().keys().contains("nrgStack")) {
				
				KeyValueParams params = !nrgParamsName.isEmpty() ?
					soImage.getParams().getNamedKeyValueParamsCollection().getNull( nrgParamsName )
					: null;
					
				SharedObjectsUtilities.writeNRGStack(
					new NRGStackWithParams(
						soImage.getStackCollection().getException("nrgStack"),
						params
					),
					outputManager,
					logErrorReporter
				);
			}
		} catch (GetOperationFailedException | CreateException e) {
			logErrorReporter.getErrorReporter().recordError(SharedObjectsUtilities.class, e);
		}
	}
		
		
	
	
	public static INamedProvider<Stack> createCombinedStacks( ImageInitParams so ) {
		return new CombineDiverseProvidersAsStacks(
			so.getStackCollection(),
			so.getChnlCollection(),
			so.getBinaryImageCollection()
		);
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
	
	public static void output(
		MPPInitParams soMPP,
		BoundOutputManagerRouteErrors outputManager,
		LogErrorReporter logErrorReporter,
		boolean suppressSubfolders
	) {
		StackCollectionOutputter.outputSubset(
			createCombinedStacks(soMPP.getImage() ),
			outputManager,
			StackOutputKeys.STACK,
			suppressSubfolders,
			logErrorReporter.getErrorReporter()
		);
		
		SubsetOutputterFactory factory = new SubsetOutputterFactory(soMPP, outputManager, suppressSubfolders);
		factory.cfg().outputSubset(logErrorReporter.getErrorReporter());
		factory.histogram().outputSubset(logErrorReporter.getErrorReporter());
		factory.objMask().outputSubset(logErrorReporter.getErrorReporter());
	}
	
	public static void outputWithException(
		MPPInitParams soMPP,
		BoundOutputManagerRouteErrors outputManager,
		boolean suppressSubfolders
	) throws OutputWriteFailedException {
		assert(outputManager.getOutputWriteSettings().hasBeenInit());	
		
		StackCollectionOutputter.outputSubsetWithException(
			createCombinedStacks(soMPP.getImage() ),
			outputManager,
			StackOutputKeys.STACK,
			suppressSubfolders
		);

		SubsetOutputterFactory factory = new SubsetOutputterFactory(soMPP, outputManager, suppressSubfolders);
		factory.cfg().outputSubsetWithException();
		factory.histogram().outputSubsetWithException();
		factory.objMask().outputSubsetWithException();
	}

	

}
