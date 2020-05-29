package org.anchoranalysis.io.generator.serialized;

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


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Optional;

import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class ObjectOutputStreamGenerator<T extends Serializable> extends SerializedIterableGenerator<T> {
	
	public ObjectOutputStreamGenerator(Optional<String> manifestFunction) {
		super(manifestFunction);
	}
	
	public ObjectOutputStreamGenerator(T rootObject, Optional<String> manifestFunction ) {
		super(rootObject, manifestFunction);
	}
	
	@Override
	protected void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath, T element)
			throws OutputWriteFailedException {

		try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
			
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject( element );
			out.close();
			fos.close();
			
		} catch (IOException e) {
			throw new OutputWriteFailedException(e);
		}
	}
	
	@Override
	protected String extensionSuffix(OutputWriteSettings outputWriteSettings) {
		return "";
	}
}
