package org.anchoranalysis.image.io.bean.stack.reader;

import java.nio.file.Path;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;
import org.anchoranalysis.image.voxel.extracter.OrientationChange;

/**
 * A {@link StackReader} that supports the correction of orientation as images are read from the
 * file-system.
 *
 * @author Owen Feehan
 */
public abstract class StackReaderOrientationCorrection extends StackReader {

    /**
     * Opens a file containing one or more images but does not read an image.
     *
     * @param path where the file is located.
     * @param orientationCorrection correction applied to the orientation as image is loaded.
     * @return an interface to the opened file that should be closed when no longer in use.
     * @throws ImageIOException if the file cannot be read.
     */
    public abstract OpenedImageFile openFile(Path path, OrientationChange orientationCorrection)
            throws ImageIOException;
}
