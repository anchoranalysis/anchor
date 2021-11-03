package org.anchoranalysis.image.io.bean.stack.reader;

import java.nio.file.Path;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;
import lombok.Getter;
import lombok.Setter;

/**
 * A {@link StackReader} that delegates to a <i>single</i> other {@link StackReader}.
 * 
 * @author Owen Feehan
 *
 */
public abstract class StackReaderUnary extends StackReader {

    // START BEAN PROPERTIES
    /** Reads the image that is subsequently flattened. */
    @BeanField @Getter @Setter private StackReader stackReader;
    // END BEAN PROPERTIES
    
    @Override
    public OpenedImageFile openFile(Path path) throws ImageIOException {
        return wrapOpenedFile(path, stackReader.openFile(path));
    }
    
    /**
     * Wraps an {@link OpenedImageFile} from the delegate.
     * 
     * @param path the path of the image-file that was opened.
     * @param openedFileFromDelegate the opened-file from the delegate.
     * @return the wrapped opened image-file with additional logic from this class.
     * @throw ImageIOException if the image cannot be opened.
     */
    protected abstract OpenedImageFile wrapOpenedFile(Path path, OpenedImageFile openedFileFromDelegate) throws ImageIOException;
}
