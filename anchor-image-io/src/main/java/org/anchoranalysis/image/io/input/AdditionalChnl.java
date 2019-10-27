package org.anchoranalysis.image.io.input;

import java.nio.file.Path;

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


import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.image.io.bean.chnl.map.ImgChnlMap;
import org.anchoranalysis.image.io.bean.input.ImgChnlMapEntry;

class AdditionalChnl {
	private String chnlName;
	private int chnlIndex;
	private Operation<Path> filePath;
	
	public AdditionalChnl(String chnlName, int chnlIndex, Operation<Path> filePath) {
		super();
		this.chnlName = chnlName;
		this.chnlIndex = chnlIndex;
		this.filePath = filePath;
	}


	public Path getFilePath() throws GetOperationFailedException {
		try {
			return filePath.doOperation();
		} catch (ExecuteException e) {
			throw new GetOperationFailedException(e);
		}
	}
	
	public ImgChnlMap createChnlMap() {
		ImgChnlMap map = new ImgChnlMap();
		map.add( new ImgChnlMapEntry(chnlName, chnlIndex) );
		return map;
	}


	public String getChnlName() {
		return chnlName;
	}
}