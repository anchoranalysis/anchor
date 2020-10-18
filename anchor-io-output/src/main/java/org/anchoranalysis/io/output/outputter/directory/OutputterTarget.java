/*-
 * #%L
 * anchor-io-output
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
package org.anchoranalysis.io.output.outputter.directory;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.path.DirectoryWithPrefix;
import org.anchoranalysis.io.output.path.PathCreator;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;

/**
 * The directory and prefix an outputter writes to.
 *
 * <p>This class is <i>immutable</i>.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class OutputterTarget {

    /** The directory to which the output-manager is bound to. */
    private BoundDirectory directory;

    @Getter private final DirectoryWithPrefix prefix;

    public OutputterTarget(DirectoryWithPrefix prefix, boolean deleteExistingDirectory)
            throws BindFailedException {
        this(new BoundDirectory(prefix.getDirectory(), deleteExistingDirectory), prefix);
    }

    /**
     * Creates a new {@link OutputterTarget} with a changed prefix.
     *
     * <p>The directory-component of the prefix must be equal to or a subdirectory of the existing
     * {@code directory}.
     *
     * @param prefixToAssign the prefix to assign
     * @return a new shallow-copied {@link OutputterTarget} but instead with {@code prefixToAssign}.
     * @throws BindFailedException if the subdirectory cannot be outputted to
     */
    public OutputterTarget changePrefix(DirectoryWithPrefix prefixToAssign)
            throws BindFailedException {
        return new OutputterTarget(
                directory.bindToSubdirectory(prefixToAssign.getDirectory()), prefixToAssign);
    }

    public Optional<WriterExecuteBeforeEveryOperation> getParentDirectoryCreator() {
        return directory.getParentDirectoryCreator();
    }

    public Path getFolderPath() {
        return prefix.getDirectory();
    }

    public PathCreator pathCreator() {
        return prefix;
    }
}
