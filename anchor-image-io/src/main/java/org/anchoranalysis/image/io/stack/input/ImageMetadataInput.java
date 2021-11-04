package org.anchoranalysis.image.io.stack.input;

import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;
import org.anchoranalysis.image.core.stack.ImageMetadata;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.io.input.file.NamedFile;
import org.anchoranalysis.io.input.file.SingleFileInputBase;

/**
 * An input that provides a {@link ImageMetadata} but only lazily when first called.
 *
 * @author Owen Feehan
 */
public class ImageMetadataInput extends SingleFileInputBase {

    /** Supplies the associated image metadata. */
    private final CheckedSupplier<ImageMetadata, ImageIOException> metadata;

    /**
     * Create for a particular file and metadata.
     *
     * @param file the file responsible for this input.
     * @param metadata the associated image metadata.
     */
    public ImageMetadataInput(
            NamedFile file, CheckedSupplier<ImageMetadata, ImageIOException> metadata) {
        super(file);
        this.metadata = CachedSupplier.cache(metadata);
    }

    /**
     * The associated image metadata.
     *
     * @return the metadata.
     * @throws ImageIOException if called when retrieving the metadata for the first time.
     */
    public ImageMetadata metadata() throws ImageIOException {
        return metadata.get();
    }
}
