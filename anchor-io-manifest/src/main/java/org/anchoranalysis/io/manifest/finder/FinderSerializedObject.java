package org.anchoranalysis.io.manifest.finder;

/*
 * #%L
 * anchor-io
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


import java.util.List;

import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.bean.deserializer.KeyValueParamsDeserializer;
import org.anchoranalysis.io.bean.deserializer.ObjectInputStreamDeserializer;
import org.anchoranalysis.io.bean.deserializer.XStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.helper.filewrite.FileWriteFileFunctionType;

public class FinderSerializedObject<ObjectType> extends FinderSingleFile {
	
	private ObjectType deserializedObject;
	private String function;
	
	public FinderSerializedObject(String function, ErrorReporter errorReporter) {
		super(errorReporter);
		this.function = function;
	}
	
	private ObjectType deserialize( FileWrite fileWrite )
			throws DeserializationFailedException {
		
		Deserializer<ObjectType> deserializer;
		if (fileWrite.getFileName().toLowerCase().endsWith(".properties.xml")) {
			deserializer = new KeyValueParamsDeserializer<ObjectType>();
		} else if (fileWrite.getFileName().toLowerCase().endsWith(".xml")) {
			deserializer = new XStreamDeserializer<>();
		} else {
			deserializer = new ObjectInputStreamDeserializer<>();
		}
			
		
		return deserializer.deserialize( fileWrite.calcPath() );
	}

	public ObjectType get() throws GetOperationFailedException {
		assert( exists() );
		if (deserializedObject==null) {
			try {
				deserializedObject = deserialize( getFoundFile() );
			} catch (DeserializationFailedException e) {
				throw new GetOperationFailedException(e);
			}
		}
		return deserializedObject;
	}


	@Override
	protected FileWrite findFile(ManifestRecorder manifestRecorder)
			throws MultipleFilesException {
		List<FileWrite> files = FinderUtilities.findListFile( manifestRecorder, new FileWriteFileFunctionType(function, "serialized") );
		
		if (files.size()==0) {
			return null;
		}
		
		// We prioritise .ser ahead of anything else
		for( FileWrite f : files) {
			if (f.getFileName().endsWith(".ser")) {
				return f;
			}
		}
		
		return files.get(0);
	}
	
	private Operation<ObjectType> operation =  new CachedOperation<ObjectType>() {

		@Override
		protected ObjectType execute() throws ExecuteException {
			try {
				if (!exists()) {
					return null;
				}
				return get();
			} catch (GetOperationFailedException e) {
				throw new ExecuteException(e);
			}
		}
		
	};
	
	public Operation<ObjectType> operation() {
		return operation;
	}
}