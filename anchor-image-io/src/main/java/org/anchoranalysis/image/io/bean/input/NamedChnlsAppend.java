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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.input.NamedChnlsInputAppend;
import org.anchoranalysis.image.io.input.NamedChnlsInputBase;
import org.anchoranalysis.io.bean.filepath.generator.FilePathGenerator;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.OperationOutFilePath;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.params.InputContextParams;


public class NamedChnlsAppend extends NamedChnlsBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private InputManager<NamedChnlsInputBase> input;
	
	@BeanField @DefaultInstance
	private RasterReader rasterReader;
	
	@BeanField @Optional
	private List<NamedBean<FilePathGenerator>> listAppend;
	
	@BeanField
	private boolean forceEagerEvaluation = false;
	
	@BeanField
	private boolean ignoreFileNotFoundAppend = false;
	
	@BeanField
	private boolean skipMissingChannels = false;
	// END BEAN PROPERTIES
	
	@Override
	public List<NamedChnlsInputBase> inputObjects(InputContextParams inputContext, ProgressReporter progressReporter)
			throws FileNotFoundException, IOException,
			DeserializationFailedException {

		try( ProgressReporterMultiple prm = new ProgressReporterMultiple(progressReporter, 2)) {
			
			Iterator<NamedChnlsInputBase> itr = input.inputObjects(inputContext, new ProgressReporterOneOfMany(prm)).iterator();
			
			prm.incrWorker();
			
			List<NamedChnlsInputBase> listTemp = new ArrayList<>();
			while( itr.hasNext() ) {
				listTemp.add( itr.next() );
			}
			
			List<NamedChnlsInputBase> outList = createOutList( listTemp, new ProgressReporterOneOfMany(prm), inputContext.isDebugMode() );
			
			prm.incrWorker();
			
			return outList;
		}
	}
	
	private List<NamedChnlsInputBase> createOutList( List<NamedChnlsInputBase> listTemp, ProgressReporter progressReporter, boolean debugMode ) throws IOException {

		progressReporter.setMin(0);
		progressReporter.setMax( listTemp.size() );
		progressReporter.open();
		
		try {
		
			List<NamedChnlsInputBase> outList = new ArrayList<>();
			for( int i=0; i<listTemp.size(); i++) {
				
				NamedChnlsInputBase ncc = listTemp.get(i);
				
				if (ignoreFileNotFoundAppend) {
					
					try {
						outList.add( append(ncc, debugMode) );		
					} catch ( IOException e) {
						
					}
					
				} else {
					outList.add( append(ncc, debugMode) );	
				}
				
				progressReporter.update(i);
			}
			return outList;
			
		} finally {
			progressReporter.close();
		}
		
	}
	
	// We assume all the input files are single channel images
	private NamedChnlsInputBase append( final NamedChnlsInputBase ncc, boolean debugMode ) throws IOException {
		
		NamedChnlsInputBase out = ncc; 
		
		if (listAppend==null) {
			return out;
		}
		
		for( final NamedBean<FilePathGenerator> ni : listAppend) {
			
			// Delayed-calculation of the appending path as it can be a bit expensive when multiplied by so many items
			CachedOperation<Path> outPath = new OperationOutFilePath(ni, ()->ncc.pathForBinding(), debugMode);
			
			if (forceEagerEvaluation) {
				try {
					Path path = outPath.doOperation();
					if (!Files.exists(path)) {
						
						if (skipMissingChannels) {
							continue;
						} else {
							throw new FileNotFoundException( String.format("Append path: %s does not exist",path) );
						}
					}
					
				} catch (ExecuteException e) {
					throw new IOException(e.getCause());
				}
			}
			
			out = new NamedChnlsInputAppend<>(out, ni.getName(), 0, outPath, rasterReader );
		}
	
		return out;
	}

	public InputManager<NamedChnlsInputBase> getInput() {
		return input;
	}

	public void setInput(InputManager<NamedChnlsInputBase> input) {
		this.input = input;
	}

	public List<NamedBean<FilePathGenerator>> getListAppend() {
		return listAppend;
	}

	public void setListAppend(List<NamedBean<FilePathGenerator>> listAppend) {
		this.listAppend = listAppend;
	}

	public boolean isForceEagerEvaluation() {
		return forceEagerEvaluation;
	}

	public void setForceEagerEvaluation(boolean forceEagerEvaluation) {
		this.forceEagerEvaluation = forceEagerEvaluation;
	}

	public boolean isIgnoreFileNotFoundAppend() {
		return ignoreFileNotFoundAppend;
	}

	public void setIgnoreFileNotFoundAppend(boolean ignoreFileNotFoundAppend) {
		this.ignoreFileNotFoundAppend = ignoreFileNotFoundAppend;
	}

	public boolean isSkipMissingChannels() {
		return skipMissingChannels;
	}

	public void setSkipMissingChannels(boolean skipMissingChannels) {
		this.skipMissingChannels = skipMissingChannels;
	}

	public RasterReader getRasterReader() {
		return rasterReader;
	}

	public void setRasterReader(RasterReader rasterReader) {
		this.rasterReader = rasterReader;
	}



}
