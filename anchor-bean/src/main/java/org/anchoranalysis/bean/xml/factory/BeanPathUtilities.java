/* (C)2020 */
package org.anchoranalysis.bean.xml.factory;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.bean.AnchorBean;

public class BeanPathUtilities {

    private BeanPathUtilities() {}

    public static Path pathRelativeToBean(AnchorBean<?> bean, String relativePath) {
        Path relative = Paths.get(relativePath);
        return combine(bean.getLocalPath(), relative);
    }

    public static Path combine(Path exstPath, Path filePath) {

        // Then we assume it's not a relative-path, take it on its own
        if (filePath.isAbsolute()) {
            return filePath;
        }

        // If our existing path isn't absolutely, we make it absolute now
        if (!exstPath.isAbsolute()) {
            exstPath = exstPath.toAbsolutePath();
        }

        // Otherwise it's a relative path and we combine it
        Path totalPath = getPathWithoutFileName(exstPath.toString()).resolve(filePath);
        assert totalPath != null;
        return totalPath.normalize();
    }

    private static Path getPathWithoutFileName(String in) {
        int index = lastIndexOfEither(in, '/', '\\');
        if (index == -1) {
            throw new IllegalArgumentException("There is no path without the filename");
        }
        return Paths.get(in.substring(0, index));
    }

    private static int lastIndexOfEither(String str, char c1, char c2) {
        return Math.max(str.lastIndexOf(c1), str.lastIndexOf(c2));
    }
}
