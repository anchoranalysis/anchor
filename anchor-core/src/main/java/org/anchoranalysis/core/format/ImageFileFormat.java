package org.anchoranalysis.core.format;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A type of image format that are read/written to the filesystem, together with the extensions it may use.
 * 
 * @author Owen Feehan
 *
 */
public enum ImageFileFormat implements FileFormat {
    
    TIFF("tif", "tiff"),
    PNG("png"),
    JPEG("jpg", "jpeg"),
    GIF("gif"),
    BMP("bmp"),
    OME_TIFF("ome.tif", "ome.tiff"),
    OME_XML("ome.xml");
    
    /** The default extension associated with a particular format. */
    private final String defaultExtension;
    
    /** An alternative extension associated with a particular format. */
    private final Optional<String> alternativeExtension;
    
    ImageFileFormat(String defaultExtension) {
        this(defaultExtension, Optional.empty());
    }
    
    ImageFileFormat(String defaultExtension, String alternativeExtension) {
        this(defaultExtension, Optional.of(alternativeExtension));
    }
    
    private ImageFileFormat(String defaultExtension, Optional<String> alternativeExtension) {
        this.defaultExtension = FormatExtensions.normalizeToLowerCase(defaultExtension);
        this.alternativeExtension = alternativeExtension.map(FormatExtensions::normalizeToLowerCase);
    }
    
    /**
     * All extensions associated with a file-format.
     * 
     * @return a stream of all extensions associated with a particular file format.
     */
    public Stream<String> allExtensions() {
        if (alternativeExtension.isPresent()) {
            return Stream.of(defaultExtension, alternativeExtension.get());
        } else {
            return Stream.of(defaultExtension);
        }
    }

    @Override
    public boolean matches(String filePath) {
        if (FormatExtensions.matches(filePath, defaultExtension)) {
            return true;
        }
        return alternativeExtension.isPresent() && FormatExtensions.matches(filePath, alternativeExtension.get());
    }

    @Override
    public String descriptiveIdentifier() {
        return getDefaultExtension();
    }

    @Override
    public String getDefaultExtension() {
        return defaultExtension;
    }
}
