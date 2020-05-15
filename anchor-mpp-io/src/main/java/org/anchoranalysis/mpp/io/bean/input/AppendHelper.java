package org.anchoranalysis.mpp.io.bean.input;

/*-
 * #%L
 * anchor-mpp-io
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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.wrap.CachedOperationWrap;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.histogram.HistogramCSVReader;
import org.anchoranalysis.image.io.objs.ObjMaskCollectionReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.io.bean.filepath.generator.FilePathGenerator;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.input.OperationOutFilePath;
import org.anchoranalysis.mpp.io.input.MultiInput;
import org.anchoranalysis.mpp.io.input.MultiInputSubMap;
import org.anchoranalysis.core.params.KeyValueParams;

class AppendHelper {
	
	/** Reads an object from a path */
	@FunctionalInterface
	private interface ReadFromPath<T> {
		T apply(Path in) throws Exception;
	}
	
	// We assume all the input files are single channel images
	public static void appendStack(
		List<NamedBean<FilePathGenerator>> listAppendStack,
		final MultiInput inputObject,
		boolean debugMode,
		final RasterReader rasterReader
	) {
		append(
			inputObject,				
			listAppendStack,
			MultiInput::stack,
			outPath -> {
				try {
					return openRaster(outPath, rasterReader);
				} catch (RasterIOException e) {
					throw new OperationFailedException(e);
				}
			},
			debugMode
		);
	}

	public static void appendHistogram(
		List<NamedBean<FilePathGenerator>> list,
		MultiInput inputObject,
		boolean debugMode
	) {
		
		append(
			inputObject,				
			list,
			MultiInput::histogram,
			outPath -> HistogramCSVReader.readHistogramFromFile( outPath ),
			debugMode
		);
	}
	
	
	public static void appendFilePath(
		List<NamedBean<FilePathGenerator>> list,
		MultiInput inputObject,
		boolean debugMode
	) {
		
		append(
			inputObject,				
			list,
			MultiInput::filePath,
			outPath -> outPath,
			debugMode
		);

	}
	
	public static void appendKeyValueParams(
		List<NamedBean<FilePathGenerator>> list,
		final MultiInput inputObject,
		boolean debugMode
	) {
		
		// Delayed-calculation of the appending path as it can be a bit expensive when multiplied by so many items		
		append(
			inputObject,				
			list,
			MultiInput::keyValueParams,
			outPath -> KeyValueParams.readFromFile(outPath),
			debugMode
		);
	}
	
	public static void appendCfg(
		List<NamedBean<FilePathGenerator>> listAppendCfg,
		final MultiInput inputObject,
		boolean debugMode
	) {
		
		append(
			inputObject,				
			listAppendCfg,
			MultiInput::cfg,
			outPath -> DeserializerHelper.deserializeCfg( outPath ),
			debugMode
		);
	}
	
	public static void appendCfgFromAnnotation(
		List<NamedBean<FilePathGenerator>> listAppendCfgFromAnnotation,
		MultiInput inputObject,
		boolean includeAccepted,
		boolean includeRejected,
		boolean debugMode
	) {
		
		append(
			inputObject,				
			listAppendCfgFromAnnotation,
			MultiInput::cfg,
			outPath -> DeserializerHelper.deserializeCfgFromAnnotation(
				outPath,
				includeAccepted,
				includeRejected
			),
			debugMode
		);
	}
	
	public static void appendObjMaskCollection(
		List<NamedBean<FilePathGenerator>> listAppendCfg,
		MultiInput inputObject,
		boolean debugMode 
	) {
		append(
			inputObject,
			listAppendCfg,
			MultiInput::objs,
			outPath -> ObjMaskCollectionReader.createFromPath(outPath),
			debugMode
		);
	}

	/**
	 * Appends new items to a particular OperationMap associated with the MultiInput
	 *   by transforming paths
	 * 
	 * @param inputObject the input-object
	 * @param list file-generations to read paths from 
	 * @param extractMap extracts an OperationMap from inputObject
	 * @param reader converts from a path to the object of interest
	 * @param debugMode
	 * @throws IOException
	 */
	private static <T> void append(
		MultiInput inputObject,			
		List<NamedBean<FilePathGenerator>> list,
		Function<MultiInput,MultiInputSubMap<T>> extractMap,
		ReadFromPath<T> reader,
		boolean debugMode
	) {
		
		for( NamedBean<FilePathGenerator> ni : list) {
			
			MultiInputSubMap<T> map = extractMap.apply(inputObject);
			
			map.add(
				ni.getName(),
				new CachedOperationWrap<T,OperationFailedException>(
					() -> readObjectForAppend(inputObject, reader, ni, debugMode)
				)
			);
		}
	}
	
	private static <T> T readObjectForAppend(
		MultiInput inputObject,
		ReadFromPath<T> reader,
		NamedBean<FilePathGenerator> ni,
		boolean debugMode
	) throws OperationFailedException {
		// Delayed-calculation of the appending path as it can be a bit expensive when multiplied by so many items
		CachedOperation<Path, AnchorIOException> outPath = new OperationOutFilePath(
			ni,
			()->inputObject.pathForBinding(),
			debugMode
		);						
		
		try {
			return reader.apply(
				outPath.doOperation()
			);
		} catch (Exception e) {
			throw new OperationFailedException("An error occured reading objects", e);
		}
	}
		
	private static TimeSequence openRaster( Path path, RasterReader rasterReader ) throws RasterIOException {
		try (OpenedRaster or = rasterReader.openFile( path )) {
			return or.open(0, ProgressReporterNull.get() );
		}
	}
}
