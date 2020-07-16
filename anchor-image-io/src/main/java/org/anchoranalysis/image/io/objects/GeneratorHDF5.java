/* (C)2020 */
package org.anchoranalysis.image.io.objects;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.SingleFileTypeGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

/**
 * Writes an object-mask-collection to a HDF5 file
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class GeneratorHDF5 extends SingleFileTypeGenerator
        implements IterableGenerator<ObjectCollection> {

    // Name of the attribute in the root of the HDF5 that stores the number of objects
    public static final String NUM_OBJECTS_ATTR_NAME = "numberObjects";

    // START REQUIRED ARGUMENTS
    private final boolean compressed;
    // END REQUIRED ARGUMENTS

    private ObjectCollection item;

    public static void writeObjectsToFile(ObjectCollection objects, Path filePath) {
        GeneratorHDF5 generator = new GeneratorHDF5(true);
        generator.setIterableElement(objects);
        generator.writeToFile(new OutputWriteSettings(), filePath);
    }

    @Override
    public Generator getGenerator() {
        return this;
    }

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath) {
        // Write a HDF file
        writeObjects(getIterableElement(), filePath);
    }

    private void writeObjects(ObjectCollection objects, Path filePath) {

        IHDF5Writer writer = HDF5Factory.open(filePath.toString());

        addObjectsSizeAttribute(writer, objects);

        try {
            for (int i = 0; i < objects.size(); i++) {

                new ObjectMaskHDF5Writer(
                                objects.get(i), HDF5PathHelper.pathForObject(i), writer, compressed)
                        .apply();
            }

        } finally {
            writer.close();
        }
    }

    // Adds an attribute with the total number of objects, so it can be quickly queried
    //  from the HDF5 without parsing all the datasets
    private void addObjectsSizeAttribute(IHDF5Writer writer, ObjectCollection objects) {
        writer.uint32().setAttr("/", NUM_OBJECTS_ATTR_NAME, objects.size());
    }

    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings) {
        return "h5";
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(
                new ManifestDescription("hdf5", ObjectCollectionWriter.MANIFEST_DESCRIPTION));
    }

    @Override
    public ObjectCollection getIterableElement() {
        return item;
    }

    @Override
    public void setIterableElement(ObjectCollection element) {
        this.item = element;
    }
}
