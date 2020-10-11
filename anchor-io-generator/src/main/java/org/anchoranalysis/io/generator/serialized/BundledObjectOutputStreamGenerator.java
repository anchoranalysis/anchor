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

package org.anchoranalysis.io.generator.serialized;

import java.io.Serializable;
import java.util.Optional;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.sequence.OutputSequenceFactory;
import org.anchoranalysis.io.generator.sequence.OutputSequenceIncrementing;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternIntegerSuffix;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.deserializer.bundle.Bundle;
import org.anchoranalysis.io.manifest.deserializer.bundle.BundleParameters;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.recorded.RecordingWriters;

public class BundledObjectOutputStreamGenerator<T extends Serializable> implements Generator<T> {

    private T element;

    private Bundle<T> bundle;

    private BundleParameters bundleParameters;

    private ObjectOutputStreamGenerator<Bundle<T>> outputGenerator;

    private final OutputSequenceIncrementing<Bundle<T>> outputSequence;

    public BundledObjectOutputStreamGenerator(
            BundleParameters bundleParameters,
            String outputName,
            int numberDigitsInOutput,
            OutputterChecked parentOutputter,
            String manifestDescriptionFunction) throws OutputWriteFailedException {
        this.bundleParameters = bundleParameters;
        
        ManifestDescription manifestDescription =
                new ManifestDescription("serializedBundle", manifestDescriptionFunction);

        this.outputGenerator =
                new ObjectOutputStreamGenerator<>(Optional.of(manifestDescriptionFunction));

        OutputPatternIntegerSuffix directory = new OutputPatternIntegerSuffix(outputName,numberDigitsInOutput,true,Optional.of(manifestDescription));
        this.outputSequence = new OutputSequenceFactory<>(outputGenerator, parentOutputter).incrementingByOne(directory);
    }

    @Override
    public void start() {
        bundle = new Bundle<>();
    }

    @Override
    public void write(OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        throw new OutputWriteFailedException(
                "this generator does not support writes without indexes");
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle, String index, OutputterChecked outputter)
            throws OutputWriteFailedException {
        bundle.add(index, element);

        // If we have reached our full capacity, then we serialize the bundle, and clear it for the
        // next set of items
        if (bundle.size() == bundleParameters.getBundleSize()) {
            outputSequence.add(bundle);
            this.bundle = new Bundle<>();
        }
        return 1;
    }

    @Override
    public void end() throws OutputWriteFailedException {
        outputSequence.add(bundle);

        if (outputSequence.isOn()) {

            Generator<?> bundleParametersGenerator =
                    new ObjectOutputStreamGenerator<>(
                            bundleParameters, Optional.of("bundleParameters"));

            RecordingWriters subfolderWriters =
                    outputSequence
                            .writers()
                            .orElseThrow(
                                    () ->
                                            new OutputWriteFailedException(
                                                    "No subfolder output-manager exists"));

            subfolderWriters
                    .permissive()
                    .write("bundleParameters", () -> bundleParametersGenerator);
        }

        outputSequence.close();
    }

    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings) {
        Optional<ManifestDescription> manifestDescription =
                outputGenerator.createManifestDescription();
        return manifestDescription.map(
                md ->
                        new FileType[] {
                            new FileType(md, outputGenerator.getFileExtension(outputWriteSettings))
                        });
    }

    @Override
    public T getElement() {
        return element;
    }

    @Override
    public void assignElement(T element) {
        this.element = element;
    }
}
