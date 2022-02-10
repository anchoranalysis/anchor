/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.object.output.hdf5;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.io.object.HDF5PathHelper;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.generator.OneStageGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

/**
 * Writes a {@link ObjectCollection} to a HDF5 file.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class HDF5ObjectsGenerator extends OneStageGenerator<ObjectCollection> {

    /** Name of the attribute in the root of the HDF5 that stores the number of objects. */
    public static final String NUMBER_OBJECTS_ATTRIBUTE_NAME = "numberObjects";

    // START REQUIRED ARGUMENTS
    /** Whether to use compression when writing the object-masks in HDF5. */
    private final boolean compressed;
    // END REQUIRED ARGUMENTS

    /** Creates with compression activated. */
    public HDF5ObjectsGenerator() {
        this.compressed = true;
    }

    @Override
    public void writeToFile(ObjectCollection element, OutputWriteSettings settings, Path filePath) {
        IHDF5Writer writer = HDF5Factory.open(filePath.toString());

        addObjectsSizeAttribute(writer, element);
        try {
            for (int i = 0; i < element.size(); i++) {

                ObjectMaskHDF5Writer writerHDF5 =
                        new ObjectMaskHDF5Writer(
                                element.get(i), pathForObject(i), writer, compressed);
                writerHDF5.writeObject();
            }

        } finally {
            writer.close();
        }
    }

    @Override
    public String selectFileExtension(OutputWriteSettings settings, Optional<Logger> logger) {
        return "h5";
    }

    /**
     * Adds an attribute with the total number of objects, so it can be quickly queried from the
     * HDF5 without parsing all the datasets.
     */
    private static void addObjectsSizeAttribute(IHDF5Writer writer, ObjectCollection objects) {
        writer.uint32().setAttr("/", NUMBER_OBJECTS_ATTRIBUTE_NAME, objects.size());
    }

    /** The path in the HDF5 file for a particular object. */
    private static String pathForObject(int index) {
        return String.format("%s/%08d", HDF5PathHelper.OBJECTS_ROOT_WITH_SEPERATORS, index);
    }
}
