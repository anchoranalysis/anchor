/*-
 * #%L
 * anchor-io-generator
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.generator.combined;

import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.generator.MultipleFileTypeGenerator;
import org.anchoranalysis.io.generator.serialized.ObjectOutputStreamGenerator;
import org.anchoranalysis.io.generator.serialized.XStreamGenerator;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

public class ManifestGenerator implements MultipleFileTypeGenerator<Manifest> {

    private final CombinedListGenerator<Manifest> delegate;
    
    public ManifestGenerator() {
        delegate = new CombinedListGenerator<>(new XStreamGenerator<>(), new ObjectOutputStreamGenerator<>());
    }

    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException {
        return delegate.getFileTypes(outputWriteSettings);
    }

    @Override
    public void write(Manifest element, OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        delegate.write(element, outputNameStyle, outputter);
    }

    @Override
    public int writeWithIndex(Manifest element, String index, IndexableOutputNameStyle outputNameStyle,
            OutputterChecked outputter) throws OutputWriteFailedException {
        return delegate.writeWithIndex(element, index, outputNameStyle, outputter);
    }

}
