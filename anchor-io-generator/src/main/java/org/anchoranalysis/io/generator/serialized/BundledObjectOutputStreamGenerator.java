/* (C)2020 */
package org.anchoranalysis.io.generator.serialized;

import java.io.Serializable;
import java.util.Optional;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceIncrementalWriter;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.deserializer.bundle.Bundle;
import org.anchoranalysis.io.manifest.deserializer.bundle.BundleParameters;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class BundledObjectOutputStreamGenerator<T extends Serializable>
        implements Generator, IterableGenerator<T> {

    private T element;

    private Bundle<T> bundle;

    private BundleParameters bundleParameters;

    private ObjectOutputStreamGenerator<Bundle<T>> outputGenerator;

    private GeneratorSequenceIncrementalWriter<Bundle<T>> generatorSequence;

    public BundledObjectOutputStreamGenerator(
            BundleParameters bundleParameters,
            IndexableOutputNameStyle indexableOutputNameStyle,
            BoundOutputManager parentOutputManager,
            String manifestDescriptionFunction) {
        this.bundleParameters = bundleParameters;

        ManifestDescription manifestDescription =
                new ManifestDescription("serializedBundle", manifestDescriptionFunction);

        outputGenerator =
                new ObjectOutputStreamGenerator<>(Optional.of(manifestDescriptionFunction));

        generatorSequence =
                new GeneratorSequenceIncrementalWriter<>(
                        parentOutputManager,
                        indexableOutputNameStyle.getOutputName(),
                        indexableOutputNameStyle,
                        outputGenerator,
                        manifestDescription,
                        0,
                        true);
    }

    @Override
    public void start() throws OutputWriteFailedException {
        bundle = new Bundle<>();
        generatorSequence.start();
    }

    @Override
    public void write(OutputNameStyle outputNameStyle, BoundOutputManager outputManager)
            throws OutputWriteFailedException {
        throw new OutputWriteFailedException(
                "this generator does not support writes without indexes");
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            String index,
            BoundOutputManager outputManager)
            throws OutputWriteFailedException {
        bundle.add(index, element);

        // If we have reached our full capacity, then we serialize the bundle, and clear it for the
        // next set of items
        if (bundle.size() == bundleParameters.getBundleSize()) {
            generatorSequence.add(bundle);
            this.bundle = new Bundle<>();
        }
        return 1;
    }

    @Override
    public void end() throws OutputWriteFailedException {
        generatorSequence.add(bundle);

        if (generatorSequence.isOn()) {

            Generator bundleParametersGenerator =
                    new ObjectOutputStreamGenerator<>(
                            bundleParameters, Optional.of("bundleParameters"));

            BoundOutputManager subfolderOutputManager =
                    generatorSequence
                            .getSubFolderOutputManager()
                            .orElseThrow(
                                    () ->
                                            new OutputWriteFailedException(
                                                    "No subfolder output-manager exists"));

            subfolderOutputManager
                    .getWriterAlwaysAllowed()
                    .write("bundleParameters", () -> bundleParametersGenerator);
        }

        generatorSequence.end();
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
    public T getIterableElement() {
        return element;
    }

    @Override
    public void setIterableElement(T element) {
        this.element = element;
    }

    @Override
    public Generator getGenerator() {
        return this;
    }
}
