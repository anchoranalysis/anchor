package org.anchoranalysis.core.format;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility functions for formatting extensions.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FormatExtensionsHelper {

    /**
     * Normalize an extension to lower-case.
     *
     * <p>This function exists to achieve normalization in a consistent way across several calls in
     * the package.
     *
     * @param extension the extension to normalize.
     * @return a lower-case version of {@code extension}.
     */
    static String normalizeToLowerCase(String extension) {
        return extension.toLowerCase();
    }
}
