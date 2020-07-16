/* (C)2020 */
package org.anchoranalysis.io.generator.serialized;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public abstract class SerializedIterableGenerator<T> extends SerializedGenerator
        implements IterableGenerator<T> {

    private T element = null;
    private Optional<String> manifestFunction;

    public SerializedIterableGenerator(Optional<String> manifestFunction) {
        super();
        this.manifestFunction = manifestFunction;
    }

    public SerializedIterableGenerator(T element, Optional<String> manifestFunction) {
        super();
        this.element = element;
        this.manifestFunction = manifestFunction;
    }

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {

        if (getIterableElement() == null) {
            throw new OutputWriteFailedException("no mutable element set");
        }

        writeToFile(outputWriteSettings, filePath, getIterableElement());
    }

    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings) {
        return outputWriteSettings.getExtensionSerialized() + extensionSuffix(outputWriteSettings);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return manifestFunction.map(mf -> new ManifestDescription("serialized", mf));
    }

    /** Writes a particular element to a file */
    protected abstract void writeToFile(
            OutputWriteSettings outputWriteSettings, Path filePath, T element)
            throws OutputWriteFailedException;

    /** Appended to the standard "serialized" extension, to form the complete extension */
    protected abstract String extensionSuffix(OutputWriteSettings outputWriteSettings);

    @Override
    public T getIterableElement() {
        return this.element;
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
