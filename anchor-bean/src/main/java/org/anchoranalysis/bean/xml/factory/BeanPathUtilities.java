/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.xml.factory;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanPathUtilities {

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
