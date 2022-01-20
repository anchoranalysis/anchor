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

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A type of image format that are read/written to the filesystem, together with the extensions it
 * may use.
 *
 * <p>Note this is not a complete list of the types of images that Anchor can read or write, rather
 * just those that are explicitly mentioned in the code-based, for operations relating to their
 * extensions.
 *
 * @author Owen Feehan
 */
public enum ImageFileFormat implements FileFormat {

    /** TIFF */
    TIFF("tif", "tiff"),

    /** PNG */
    PNG("png"),

    /** JPEG */
    JPEG("jpg", "jpeg"),

    /** GIF */
    GIF("gif"),

    /** Windows Bitmap - note that this comes in many variants. */
    BMP("bmp"),

    /** OME TIFF */
    OME_TIFF("ome.tif", "ome.tiff"),

    /** OME XML */
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
     * <p>This should not otherwise appear in either {@code defaultExtension} or {@code
     * alternativeExtension} for any other enumerated format.
     */
    private final Optional<String> alternativeExtension;

    /**
     * Creates only with a default-extension.
     *
     * @param defaultExtension the default extension associated with the created format (which
     *     should be unique across all enumerated formats), without any leading period.
     */
    ImageFileFormat(String defaultExtension) {
        this(defaultExtension, Optional.empty());
    }

    /**
     * Creates with a default extension and an alternative extension.
     *
     * @param defaultExtension the default extension associated with the created format (which
     *     should be unique across all enumerated formats), without any leading period.
     * @param alternativeExtension an alternative extension associated with the created format
     *     (which should not otherwise be used in another format), without any leading period.
     */
    ImageFileFormat(String defaultExtension, String alternativeExtension) {
        this(defaultExtension, Optional.of(alternativeExtension));
    }

    private ImageFileFormat(String defaultExtension, Optional<String> alternativeExtension) {
        this.defaultExtension = FormatExtensionsHelper.normalizeToLowerCase(defaultExtension);
        this.alternativeExtension =
                alternativeExtension.map(FormatExtensionsHelper::normalizeToLowerCase);
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
        return alternativeExtension.isPresent()
                && FormatExtensions.matches(filePath, alternativeExtension.get());
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
        return alternativeExtension.isPresent()
                && identifierNormalized.equalsIgnoreCase(alternativeExtension.get());
    }
}
