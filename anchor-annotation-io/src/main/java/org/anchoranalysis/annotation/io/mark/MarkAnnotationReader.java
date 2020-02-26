package org.anchoranalysis.annotation.io.mark;



/*
 * #%L
 * anchor-annotation
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


import java.nio.file.Files;
import java.nio.file.Path;

import org.anchoranalysis.annotation.io.AnnotationReader;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.io.bean.deserializer.XStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.error.AnchorIOException;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.io.CfgDeserializer;

public class MarkAnnotationReader implements AnnotationReader<MarkAnnotation> {

	private boolean acceptUnfinished;
	
	public MarkAnnotationReader(boolean acceptUnfinished) {
		super();
		this.acceptUnfinished = acceptUnfinished;
	}

	public boolean annotationExistsCorrespondTo( Path annotationPath ) {
		return fileNameToRead(annotationPath)!=null;
	}
	
	@Override
	public MarkAnnotation read( Path path ) throws AnchorIOException {
		
		Path pathMaybeChanged = fileNameToRead(path);
		
		if (pathMaybeChanged==null) {
			return null;
		}
		
		try {
			return readAnnotationFromPath(pathMaybeChanged);
		} catch (DeserializationFailedException e) {
			throw new AnchorIOException("Cannot deserialize annotation", e);
		}
	}
		
	// Reads an annotation if it can, returns NULL otherwise
	public Cfg readDefaultCfg( Path path ) throws DeserializationFailedException {
		
		if (path==null) {
			return null;
		}
		
		CfgDeserializer deserialized = new CfgDeserializer();
		return deserialized.deserialize(path);
	}

	private Path fileNameToRead( Path annotationPath ) {
		
		if (Files.exists(annotationPath)) {
			return annotationPath;
		}
		
		if (!acceptUnfinished) {
			return null;
		}
		
		Path pathUnfinished = TempPathCreator.deriveTempPath(annotationPath);
		
		if (Files.exists(pathUnfinished)) {
			return pathUnfinished;
		}

		// No path to read
		return null;
	}

	
	private MarkAnnotation readAnnotationFromPath( Path annotationPath ) throws DeserializationFailedException {
		XStreamDeserializer<MarkAnnotation> deserialized = new XStreamDeserializer<>();
		return deserialized.deserialize(annotationPath);
	}

}