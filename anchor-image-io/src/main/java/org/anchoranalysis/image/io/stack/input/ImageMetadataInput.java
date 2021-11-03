package org.anchoranalysis.image.io.stack.input;

import lombok.Getter;
import org.anchoranalysis.image.core.stack.ImageMetadata;
import org.anchoranalysis.io.input.file.NamedFile;
import org.anchoranalysis.io.input.file.SingleFileInputBase;

/**
 * An input that provides a {@link ImageMetadata}.
 *
 * @author Owen Feehan
 */
public class ImageMetadataInput extends SingleFileInputBase {

    /** The associated image metadata. */
    @Getter private final ImageMetadata metadata;

    /**
     * Create for a particular file and metadata.
     *
     * @param file the file responsible for this input.
     * @param metadata the associated image metadata.
     */
    public ImageMetadataInput(NamedFile file, ImageMetadata metadata) {
        super(file);
        this.metadata = metadata;
    }
}
