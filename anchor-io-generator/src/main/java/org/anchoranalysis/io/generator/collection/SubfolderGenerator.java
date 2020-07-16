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
/* (C)2020 */
package org.anchoranalysis.io.generator.collection;

import java.util.Collection;
import java.util.Optional;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * @author Owen Feehan
 * @param <T>
 * @param <S> collection-type
 */
public class SubfolderGenerator<T, S extends Collection<T>>
        implements Generator, IterableGenerator<S> {

    private S element;

    private IterableGenerator<T> generator;
    private String collectionOutputName;

    public SubfolderGenerator(IterableGenerator<T> generator, String collectionOutputName) {
        super();
        this.generator = generator;
        this.collectionOutputName = collectionOutputName;
    }

    @Override
    public void write(OutputNameStyle outputNameStyle, BoundOutputManager outputManager)
            throws OutputWriteFailedException {

        String filePhysicalName = outputNameStyle.getPhysicalName();
        IterableGeneratorWriter.writeSubfolder(
                outputManager, filePhysicalName, collectionOutputName, generator, element, false);
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            String index,
            BoundOutputManager outputManager)
            throws OutputWriteFailedException {

        String filePhysicalName = outputNameStyle.getPhysicalName(index);

        IterableGeneratorWriter.writeSubfolder(
                outputManager, filePhysicalName, collectionOutputName, generator, element, false);
        return 1;
    }

    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings) {
        return generator.getGenerator().getFileTypes(outputWriteSettings);
    }

    @Override
    public S getIterableElement() {
        return element;
    }

    @Override
    public void setIterableElement(S element) throws SetOperationFailedException {
        this.element = element;
    }

    @Override
    public Generator getGenerator() {
        return this;
    }

    public static ManifestDescription createManifestDescription(String type) {
        return new ManifestDescription("subfolder", type);
    }
}
