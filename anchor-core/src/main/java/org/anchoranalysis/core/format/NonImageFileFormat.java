/*-
 * #%L
 * anchor-core
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
package org.anchoranalysis.core.format;

/**
 * Non image file-formats that are read or writeten to the filesystem, each with an associated
 * extension.
 *
 * <p>See instead {@link ImageFileFormat} for an image file format.
 *
 * @author Owen Feehan
 */
public enum NonImageFileFormat implements FileFormat {

    /** CSV (Comma Separated Value) files. */
    CSV("csv"),

    /** HDF5 */
    HDF5("h5"),

    /** XML */
    XML("xml"),

    /** Text files. */
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
    public boolean matchesEnd(String filePath) {
        return FormatExtensions.matches(filePath, extension);
    }

    /**
     * The extension <b>without</b> any leading period.
     *
     * @return the extension (lower-case) without a period.
     */
    public String extensionWithoutPeriod() {
        return extension;
    }

    /**
     * The extension (without a period) as an array.
     *
     * @return a newly created array with one element only, the extension (without a period).
     */
    public String[] extensionAsArray() {
        return new String[] {extensionWithoutPeriod()};
    }

    /**
     * The extension <b>with</b> a leading period.
     *
     * @return the extension (lower-case) with a period.
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

    @Override
    public boolean matchesIdentifier(String identifier) {
        return FormatExtensions.removeAnyLeadingPeriod(identifier).equalsIgnoreCase(extension);
    }
}
