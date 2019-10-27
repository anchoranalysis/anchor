package org.anchoranalysis.image.io.bean.input;

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


import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.input.NamedChnlsInputAsStack;
import org.anchoranalysis.image.io.input.StackSequenceInput;
import org.anchoranalysis.image.io.input.series.NamedChnlCollectionForSeries;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * Manager that converts NamedChnlInput to StackSequenceInput
 * 
 * @author Owen Feehan
 *
 */
public class ConvertNamedChnlToStack extends InputManager<StackSequenceInput> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private InputManager<NamedChnlsInputAsStack> input;
	
	@BeanField
	private String chnlName;
	
	@BeanField
	private int timeIndex = 0;
	// END BEAN PROPERTIES
	
	

	/**
	 * An input object that converts StackNamedChnlInputObject to StackSequenceInputObject
	 * 
	 * @author Owen Feehan
	 *
	 */
	private class ConvertInputObject extends StackSequenceInput {

		private NamedChnlsInputAsStack in;
				
		public ConvertInputObject(NamedChnlsInputAsStack in) {
			super();
			this.in = in;
		}

		@Override
		public String descriptiveName() {
			return in.descriptiveName();
		}

		@Override
		public Path pathForBinding() {
			return in.pathForBinding();
		}

		@Override
		public void close(ErrorReporter errorReporter) {
			in.close(errorReporter);
		}

		@Override
		public OperationWithProgressReporter<TimeSequence> createStackSequenceForSeries(
				int seriesNum) throws RasterIOException {
			return new OperationConvert(in, seriesNum);			
		}
				

		@Override
		public void addToStore(NamedProviderStore<TimeSequence> stackCollection,
				int seriesNum, ProgressReporter progressReporter)
				throws OperationFailedException {
			in.addToStore(stackCollection, seriesNum, progressReporter);
		}

		@Override
		public void addToStoreWithName(String name,
				NamedProviderStore<TimeSequence> stackCollection, int seriesNum, ProgressReporter progressReporter)
				throws OperationFailedException {
			in.addToStoreWithName(name, stackCollection, seriesNum, progressReporter);
		}

		@Override
		public int numFrames() throws OperationFailedException {
			return in.numFrames();
		}
	}
	
	
	/**
	 * The operation of doing the conversion
	 * 
	 * @author Owen Feehan
	 *
	 */
	private class OperationConvert implements OperationWithProgressReporter<TimeSequence> {
		
		private int seriesNum;
		private NamedChnlsInputAsStack in;
					
		public OperationConvert(NamedChnlsInputAsStack in, int seriesNum) {
			super();
			this.seriesNum = seriesNum;
			this.in = in;
		}

		@Override
		public TimeSequence doOperation(
				ProgressReporter progressReporter)
				throws ExecuteException {
			
			try (ProgressReporterMultiple prm = new ProgressReporterMultiple(progressReporter, 2)) {
			
				NamedChnlCollectionForSeries ncc = in.createChnlCollectionForSeries(seriesNum, new ProgressReporterOneOfMany(prm) );
				prm.incrWorker();
				Chnl chnl = ncc.getChnl(chnlName, timeIndex, new ProgressReporterOneOfMany(prm) );
				
				TimeSequence ts = new TimeSequence();
				ts.add( new Stack(chnl) );
				return ts;
			} catch (RasterIOException e) {
				throw new ExecuteException(e);
			}
		}
	}
	
	@Override
	public List<StackSequenceInput> inputObjects(InputContextParams inputContext,
			ProgressReporter progressReporter)
			throws IOException, DeserializationFailedException {
		return input.inputObjects(inputContext, progressReporter).stream().map( this::convert ).collect( Collectors.toList() );
	}
	
	private StackSequenceInput convert( NamedChnlsInputAsStack in ) {
		return new ConvertInputObject(in);
	}
	
	public String getChnlName() {
		return chnlName;
	}

	public void setChnlName(String chnlName) {
		this.chnlName = chnlName;
	}

	public InputManager<NamedChnlsInputAsStack> getInput() {
		return input;
	}

	public void setInput(InputManager<NamedChnlsInputAsStack> input) {
		this.input = input;
	}

	public int getTimeIndex() {
		return timeIndex;
	}

	public void setTimeIndex(int timeIndex) {
		this.timeIndex = timeIndex;
	}
	
}
