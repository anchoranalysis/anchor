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


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

import com.thoughtworks.xstream.XStream;

public class XStreamGenerator<T> extends SerializedIterableGenerator<T> {
	
	public XStreamGenerator( String manifestFunction ) {
		super(manifestFunction);
	}
	
	public XStreamGenerator(T rootObject, String manifestFunction) {
		super(rootObject, manifestFunction);
	}

	public static <T> void writeObjectToFile(T rootObject, Path filePath ) throws FileNotFoundException, IOException {
		XStream xstream = new XStream();
		
		try( FileOutputStream fos = new FileOutputStream(filePath.toFile()) ) {
			try( PrintWriter pw = new PrintWriter(fos)) {
				String xml = xstream.toXML(rootObject);
				pw.write(xml);
			}
		}
	}
	
	@Override
	protected void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath, T element)
			throws OutputWriteFailedException {
		try {
			writeObjectToFile( getIterableElement(), filePath );
		} catch (IOException e) {
			throw new OutputWriteFailedException(e);
		}
	}

	@Override
	protected String extensionSuffix(OutputWriteSettings outputWriteSettings) {
		return "." + outputWriteSettings.getExtensionXML();
	}
}
