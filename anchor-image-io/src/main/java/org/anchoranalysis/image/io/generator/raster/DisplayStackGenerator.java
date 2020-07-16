/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class DisplayStackGenerator extends ObjectGenerator<DisplayStack>
        implements IterableObjectGenerator<DisplayStack, DisplayStack> {

    private StackGenerator delegate;
    private DisplayStack item;

    public DisplayStackGenerator(String manifestFunction) {
        delegate = new StackGenerator(manifestFunction);
    }

    @Override
    public void start() throws OutputWriteFailedException {
        delegate.start();
    }

    @Override
    public void end() throws OutputWriteFailedException {
        delegate.end();
    }

    @Override
    public DisplayStack getIterableElement() {
        return item;
    }

    @Override
    public void setIterableElement(DisplayStack element) throws SetOperationFailedException {
        this.item = element;

        delegate.setIterableElement(element.createImgStack(false));
    }

    @Override
    public ObjectGenerator<DisplayStack> getGenerator() {
        return this;
    }

    @Override
    public DisplayStack generate() throws OutputWriteFailedException {
        return item;
    }

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {
        delegate.writeToFile(outputWriteSettings, filePath);
    }

    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings) {
        return delegate.getFileExtension(outputWriteSettings);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return delegate.createManifestDescription();
    }
}
