/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.bean.shared.path;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.system.path.PathDifference;
import org.anchoranalysis.core.system.path.PathDifferenceException;
import org.anchoranalysis.core.system.path.SplitPath;

/**
 * Defines a <b>root path</b>.
 *
 * <p>This a directory in which files are read/written during analysis.
 *
 * <p>Analysis scripts may select different root-paths depending on how they are executed (e.g.
 * locally writing to the filesystem during debugging on a single-file, but to a server for many
 * files).
 *
 * <p>The name of a root must not be unique, but the combination of all fields should be unique i.e.
 * several roots can have the same name, but should vary in their other settings.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = false)
public class RootPath extends AnchorBean<RootPath> {

    // START PROPERTIES
    /** The name of the root-path, which need not be unique in itself. */
    @BeanField @Getter @Setter private String name;

    /*** A path on a file-system to the directory, that defines the root. */
    @BeanField @Getter @Setter private String path;

    /*** If true this root is preferred, when executing a job in debugging mode. */
    @BeanField @Getter @Setter private boolean debug = false;

    // END PROPERTIES

    /**
     * It splits the <i>root</i> portion of a path from the remainder.
     *
     * @param path path to split.
     * @return the split-path.
     * @throws PathDifferenceException if the path cannot be matched against the root.
     */
    public SplitPath split(Path path) throws PathDifferenceException {

        Path rootPath = asPath();

        // We get the difference of what is left, or else an exception is thrown if it cannot match
        PathDifference difference = PathDifference.differenceFrom(rootPath, path);
        return new SplitPath(rootPath, difference.combined());
    }

    /**
     * Exposes the {@code path} field as a {@link Path}.
     *
     * @return the path.
     */
    public Path asPath() {
        return Paths.get(path);
    }
}
