package org.anchoranalysis.core.format;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A type of image format that are read/written to the filesystem, together with the extensions it may use.
 * 
 * <p>Note this is not a complete list of the types of images that Anchor can read or write,
 * rather just those that are explicitly mentioned in the code-based, for operations relating
 * to their extensions.
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
    
    /** 
     * The default extension associated with a particular format.
     *
     * <p>This should be unique across all formats enumerated in {@link ImageFileFormat}.
     */
    private final String defaultExtension;
    
    /** 
     * An alternative extension associated with a particular format.
     *
     * <p>This should not otherwise appear in either {@code defaultExtension} or {@code alternativeExtension} for any other enumerated format.
     */
    private final Optional<String> alternativeExtension;
    
    /**
     * Creates only with a default-extension.
     *  
     * @param defaultExtension the default extension associated with the created format (which should be unique across all enumerated formats), without any leading period.
     */
    ImageFileFormat(String defaultExtension) {
        this(defaultExtension, Optional.empty());
    }
    
    /**
     * Creates with a default extension and an alternative extension.
     * 
     * @param defaultExtension the default extension associated with the created format (which should be unique across all enumerated formats), without any leading period.
     * @param alternativeExtension an alternative extension associated with the created format (which should not otherwise be used in another format), without any leading period.
     */
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
    public boolean matchesEnd(String filePath) {
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

    @Override
    public boolean matchesIdentifier(String identifier) {
        String identifierNormalized = FormatExtensions.removeAnyLeadingPeriod(identifier);
        if (identifierNormalized.equalsIgnoreCase(defaultExtension)) {
            return true;
        }
        return alternativeExtension.isPresent() && identifierNormalized.equalsIgnoreCase(alternativeExtension.get());
    }
}
