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

import java.nio.file.Path;

import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.io.bean.deserializer.XStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

class DeserializerHelper {

	private static XStreamDeserializer<Cfg> deserializerCfg = new XStreamDeserializer<>();
	private static XStreamDeserializer<MarkAnnotation> deserializerAnnotation = new XStreamDeserializer<>();
	
	public static Cfg deserializeCfg( Path path ) throws ExecuteException {
		try {
			return deserializerCfg.deserialize( path );
		} catch (DeserializationFailedException e) {
			throw new ExecuteException(e);
		}
	}
	
	public static Cfg deserializeCfgFromAnnotation( Path outPath, boolean includeAccepted, boolean includeRejected ) throws ExecuteException {
		try {
			MarkAnnotation ann = deserializerAnnotation.deserialize( outPath );
			if (!ann.isFinished()) {
				throw new ExecuteException( new DeserializationFailedException("Annotation was never finished") );
			}
			if (!ann.isAccepted()) {
				throw new ExecuteException( new DeserializationFailedException("Annotation was never accepted") );
			}
			
			Cfg cfgOut = new Cfg();
			
			if (includeAccepted) {
				cfgOut.addAll(ann.getCfg());
			}
			
			if (includeRejected && ann.getCfgReject()!=null) {
				cfgOut.addAll(ann.getCfgReject());
			}
			
			return cfgOut;
		} catch (DeserializationFailedException e) {
			throw new ExecuteException(e);
		}	
	}
}
