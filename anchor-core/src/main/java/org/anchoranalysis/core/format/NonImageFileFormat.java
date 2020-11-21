package org.anchoranalysis.core.format;

/**
 * Non image file-formats that are read or writeten to the filesystem, each with an associated extension.
 * 
 * <p>See instead {@link ImageFileFormat} for an image file format.
 * 
 * @author Owen Feehan
 *
 */
public enum NonImageFileFormat implements FileFormat {

    CSV("csv"),
    HDF5("h5"),
    XML("xml"),
    TEXT("txt"),
    /** XML for storing properties (key/value pairs). */
    PROPERTIES_XML("properties.xml"),
    /** XML for storing serialized XML (via XStream). */
    SERIALIZED_XML("ser.xml"),
    /** Binary serialization format from Java native serialization. */
    SERIALIZED_BINARY("ser");
    
    /** The extension associated with a particular format. */
    private final String extension;
    
    NonImageFileFormat(String extension) {
        this.extension = FormatExtensions.normalizeToLowerCase(extension);
    }
  
    @Override
    public boolean matches(String filePath) {
        return FormatExtensions.matches(filePath, extension);
    }
    
    /**
     * The extension <b>without</b> any leading period.
     * 
     * @return the extension (lower-case) without a period
     */
    public String extensionWithoutPeriod() {
        return extension;
    }
    
    /**
     * The extension (without a period) as an array.
     * @return a newly created array with one element only, the extension (without a period).
     */
    public String[] extensionAsArray() {
        return new String[] {extensionWithoutPeriod()};
    }
    
    /**
     * The extension <b>with</b> a leading period.
     * 
     * @return the extension (lower-case) with a period
     */
    public String extensionWithPeriod() {
        return "." + extension;
    }


    @Override
    public String descriptiveIdentifier() {
        return extensionWithoutPeriod();
    }


    @Override
    public String getDefaultExtension() {
        return extensionWithoutPeriod();
    }
}
