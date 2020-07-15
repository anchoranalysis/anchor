package org.anchoranalysis.io.bean.provider.keyvalueparams;

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


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.bean.provider.file.FileProvider;
import org.anchoranalysis.io.error.FileProviderException;
import org.anchoranalysis.io.params.InputContextParams;

import lombok.Getter;
import lombok.Setter;

public class KeyValueParamsProviderFromFile extends KeyValueParamsProvider {
	
	// START BEAN PROPERTIES
	@BeanField @Getter @Setter
	private FileProvider fileProvider;
	// END BEAN PROPERTIES

	@Override
	public KeyValueParams create() throws CreateException {
		try {
			Collection<File> files = fileProvider.create(
				new InputManagerParams(
					new InputContextParams(),						
					ProgressReporterNull.get(),
					getLogger()
				)
			);
			
			if (files.isEmpty()) {
				throw new CreateException("No files are provided");
			}
			
			if (files.size()>1) {
				throw new CreateException("More than one file is provided");
			}
			
			Path filePath = files.iterator().next().toPath();
			return KeyValueParams.readFromFile( filePath );
				
		} catch (IOException e) {
			throw new CreateException(e);
		} catch (FileProviderException e) {
			throw new CreateException("Cannot find files", e);
		}
	}
}
