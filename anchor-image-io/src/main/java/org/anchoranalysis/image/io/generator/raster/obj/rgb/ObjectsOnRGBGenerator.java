/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.obj.rgb;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * A base class for generators that draw objects on top of a RGB-Stack
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public abstract class ObjectsOnRGBGenerator extends RasterGenerator
        implements IterableObjectGenerator<ObjectCollectionWithProperties, Stack> {

    private static final ManifestDescription MANIFEST_DESCRIPTION =
            new ManifestDescription("raster", "rgbObjects");

    // START REQUIRED ARGUMENTS
    private final DrawObject drawObject;
    private final ObjectDrawAttributes attributes;

    @Getter @Setter private Optional<DisplayStack> background;
    // END REQUIRED ARGUMENTS

    // Iterable element
    private ObjectCollectionWithProperties element;

    public ObjectsOnRGBGenerator(
            DrawObject drawObject,
            ObjectDrawAttributes attributes,
            Optional<DisplayStack> background) {
        super();
        this.drawObject = drawObject;
        this.attributes = attributes;
        this.background = background;
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {
        try {
            if (!background.isPresent()) {
                throw new OutputWriteFailedException(
                        "A background is required for this generator, but has not been set.");
            }

            RGBStack backgroundRGB = generateBackground(background.get());
            drawObject.write(generateMasks(), backgroundRGB, attributes);
            return backgroundRGB.asStack();
        } catch (OperationFailedException | CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public ObjectCollectionWithProperties getIterableElement() {
        return element;
    }

    @Override
    public void setIterableElement(ObjectCollectionWithProperties element) {
        this.element = element;
    }

    @Override
    public ObjectGenerator<Stack> getGenerator() {
        return this;
    }

    @Override
    public void start() throws OutputWriteFailedException {
        // NOTHING TO DO
    }

    @Override
    public void end() throws OutputWriteFailedException {
        // NOTHING TO DO
    }

    @Override
    public boolean isRGB() {
        return true;
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(MANIFEST_DESCRIPTION);
    }

    protected abstract RGBStack generateBackground(DisplayStack background) throws CreateException;

    protected abstract ObjectCollectionWithProperties generateMasks() throws CreateException;
}
