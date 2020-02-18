package org.anchoranalysis.mpp.io.input;

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


import java.nio.file.Path;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.io.input.StackInput;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequenceStore;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

public class MultiInput extends StackInput {

	private StackWithMap stackWithMap;
	
	private OperationMap<Cfg> mapCfg = new OperationMap<>();
	private OperationMap<ObjMaskCollection> mapObjMaskCollection = new OperationMap<>();
	private OperationMap<KeyValueParams> mapKeyValueParams = new OperationMap<>();
	private OperationMap<Histogram> mapHistogram = new OperationMap<>();
	private OperationMap<Path> mapFilePath = new OperationMap<>();
	
	public MultiInput( String mainObjectName, StackInput mainInputObject ) {
		super();
		assert(mainInputObject!=null);
		this.stackWithMap = new StackWithMap(mainObjectName, mainInputObject);
	}

	@Override
	public void addToStore(NamedProviderStore<TimeSequence> stackCollection,
			int seriesNum, ProgressReporter progressReporter)
			throws OperationFailedException {
		stackWithMap.addToStore(
			stackCollection,
			seriesNum,
			progressReporter
		);
	}


	@Override
	public void addToStoreWithName(String name,
			NamedProviderStore<TimeSequence> stackCollection, int seriesNum, ProgressReporter progressReporter) throws OperationFailedException {
		throw new OperationFailedException("Not supported");
	}
	
	
	public void addToSharedObjects( MPPInitParams soMPP, ImageInitParams soImage ) throws OperationFailedException {
		
		cfg().addToStore( soMPP.getCfgCollection() );
		stack().addToStore( new WrapStackAsTimeSequenceStore(soImage.getStackCollection()) );
		objs().addToStore( soImage.getObjMaskCollection() );
		keyValueParams().addToStore( soImage.getParams().getNamedKeyValueParamsCollection() );
		filePath().addToStore( soImage.getParams().getNamedFilePathCollection() );
		histogram().addToStore( soImage.getHistogramCollection() );
	}

	@Override
	public String descriptiveName() {
		return stackWithMap.descriptiveName();
	}

	@Override
	public Path pathForBinding() {
		return stackWithMap.pathForBinding();
	}

	@Override
	public void close(ErrorReporter errorReporter) {
		stackWithMap.close(errorReporter);
		
		// We set all these objects to NULL so the garbage collector can free up memory
		// This probably isn't necessary, as the MultiInput object should get garbage-collected ASAP
		//   but just in case
		mapCfg = null;
		mapObjMaskCollection = null;
		mapKeyValueParams = null;
		mapHistogram = null;
		mapFilePath = null;
	}

	public MultiInputSubMap<Cfg> cfg() {
		return mapCfg;
	}

	public MultiInputSubMap<ObjMaskCollection> objs() {
		return mapObjMaskCollection;
	}

	public MultiInputSubMap<KeyValueParams> keyValueParams() {
		return mapKeyValueParams;
	}

	public MultiInputSubMap<Histogram> histogram() {
		return mapHistogram;
	}

	public MultiInputSubMap<Path> filePath() {
		return mapFilePath;
	}

	public MultiInputSubMap<TimeSequence> stack() {
		return stackWithMap;
	}

	public String getMainObjectName() {
		return stackWithMap.getMainObjectName();
	}

	@Override
	public int numFrames() throws OperationFailedException {
		return stackWithMap.numFrames();
	}
}
