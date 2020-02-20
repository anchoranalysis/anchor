package ch.ethz.biol.cell.mpp.feedback;

/*
 * #%L
 * anchor-mpp-sgmn
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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceNonIncrementalWriter;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

import ch.ethz.biol.cell.mpp.nrg.CfgNRGPixelized;

public abstract class PeriodicSubfolderReporter<T> extends ReporterInterval<CfgNRGPixelized> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5947180574365100731L;

	// START BEAN PROPERTIES
	@BeanField
	private String outputName;
	// END BEAN PROPER
	
	private GeneratorSequenceNonIncrementalWriter<T> sequenceWriter;
	
	private BoundOutputManagerRouteErrors parentOutputManager;
	
	
	public PeriodicSubfolderReporter() {
		super();
	}
	
	// We generate an OutputName class from the outputName string
	protected IndexableOutputNameStyle generateOutputNameStyle() {
		return new IntegerSuffixOutputNameStyle(outputName,"_%010d");
	}
	
	// We setup the manifest from an IterableGenerator
	protected IncrementalSequenceType init( IterableGenerator<T> iterableGenerator ) throws OutputWriteFailedException {
		
		IncrementalSequenceType sequenceType = new IncrementalSequenceType();
		sequenceType.setIncrementSize( getAggInterval() );
		sequenceType.setStart(0);
		
		IndexableOutputNameStyle outputStyle = generateOutputNameStyle(); 
		this.sequenceWriter = new GeneratorSequenceNonIncrementalWriter<>(
			getParentOutputManager().getDelegate(),
			outputStyle.getOutputName(),
			outputStyle,
			iterableGenerator,
			true
		);
		
		this.sequenceWriter.start( sequenceType, -1 );
		
		return sequenceType;
	}
	
	// Inner class to handle period receiver updates
	private class PeriodReceiver implements IPeriodReceiver<CfgNRGPixelized> {
		
		@Override
		public void periodStart( Reporting<CfgNRGPixelized> reporting ) throws PeriodReceiverException {
			
			try {
				sequenceWriter.add(
					generateIterableElement(reporting),
					String.valueOf( reporting.getIter() )
				);
			} catch (OutputWriteFailedException e) {
				throw new PeriodReceiverException(e);
			}
		}
		
		@Override
		public void periodEnd( Reporting<CfgNRGPixelized> reporting ) {
		}
	}

	
	@Override
	public void reportBegin(OptimizationFeedbackInitParams<CfgNRGPixelized> optInit) throws ReporterException {
		
		this.parentOutputManager = optInit.getInitContext().getOutputManager();
		
		// Let's only do this if the output is allowed
		if (!getParentOutputManager().isOutputAllowed(outputName)) {
			return;
		}
		
		optInit.getPeriodTriggerBank().obtain(getAggInterval(), new PeriodReceiver() );
	}

	protected abstract T generateIterableElement( Reporting<CfgNRGPixelized> reporting );

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	protected OutputWriteSettings getOutputWriteSettings() {
		return this.getParentOutputManager().getOutputWriteSettings();
	}
	
	protected BoundOutputManager getSubFolderOutputManager() {
		return sequenceWriter.getSubFolderOutputManager();
	}
	
	protected BoundOutputManagerRouteErrors getParentOutputManager() {
		return parentOutputManager;
	}

	@Override
	public void reportEnd(OptimizationFeedbackEndParams<CfgNRGPixelized> optStep) throws ReporterException {

		try {
			this.sequenceWriter.end();
		} catch (OutputWriteFailedException e) {
			throw new ReporterException(e);
		}
	}
}
