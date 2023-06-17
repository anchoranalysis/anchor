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

import com.google.common.base.Preconditions;
import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.DirectoryCreationParameters;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;

/**
 * A particular directory that is used as a binding-path by {@link
 * org.anchoranalysis.io.output.outputter.OutputterChecked}.
 *
 * <p>It is accompanied by a {@link LazyDirectoryCreatorPool} that an error/deletion occurs before
 * any outputs are written to a directory that already exists.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class BoundDirectory {

    /** A pool with a unique {@link LazyDirectoryCreator} for every directory. */
    private final LazyDirectoryCreatorPool directoryCreator;

    /** Parent directory creator to be executed before any derived sub-directories */
    @Getter private final Optional<WriterExecuteBeforeEveryOperation> parentDirectoryCreator;

    /**
     * Creates for a particular directory.
     *
     * @param directoryPath the path to the directory
     * @param parameters parameters that influence how a directory is created.
     * @throws BindFailedException
     */
    public BoundDirectory(Path directoryPath, DirectoryCreationParameters parameters)
            throws BindFailedException {
        this.directoryCreator = new LazyDirectoryCreatorPool(directoryPath, parameters);
        this.parentDirectoryCreator = creatorForDirectory(directoryPath, Optional.empty());
    }

    /**
     * Creates a new {@link BoundDirectory} that is a subdirectory of the existing directory.
     *
     * @param subdirectoryPath subdirectory of existing {@link BoundDirectory} for which a new
     *     {@link BoundDirectory} will be created.
     * @return a newly created {@link BoundDirectory} bound to {@code directoryPath}.
     * @throws BindFailedException when an outputter cannot be successfully bound to an output directory.
     */
    public BoundDirectory bindToSubdirectory(Path subdirectoryPath) throws BindFailedException {
        Preconditions.checkArgument(rootDirectoryContains(subdirectoryPath));
        return new BoundDirectory(
                directoryCreator, creatorForDirectory(subdirectoryPath, parentDirectoryCreator));
    }

    private Optional<WriterExecuteBeforeEveryOperation> creatorForDirectory(
            Path subdirectoryPath, Optional<WriterExecuteBeforeEveryOperation> opBefore)
            throws BindFailedException {
        try {
            return Optional.of(directoryCreator.getOrCreate(subdirectoryPath, opBefore));
        } catch (GetOperationFailedException e) {
            throw new BindFailedException(e);
        }
    }

    private boolean rootDirectoryContains(Path path) {
        return path.normalize().toString().contains(directoryCreator.getRootDirectory().toString());
    }
}
