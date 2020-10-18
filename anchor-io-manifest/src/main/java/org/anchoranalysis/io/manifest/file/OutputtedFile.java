/*-
 * #%L
 * anchor-io-manifest
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

package org.anchoranalysis.io.manifest.file;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.directory.MutableDirectory;

/**
 * An entry in a {@link Manifest} for an outputted file from an experiment.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class OutputtedFile implements Serializable {

    /** */
    private static final long serialVersionUID = 5796355859093885433L;

    // START REQUIRED ARGUMENTS
    @Getter private final MutableDirectory parentFolder;

    @Getter private final String fileName;

    @Getter private final String outputName;

    @Getter private final String index;

    /**
     * A description associated with the file.
     *
     * <p>Note that this can be null, but we avoid {@link Optional} as cannot be serialized.
     */
    @Nullable private final ManifestDescription description;
    // END REQUIRED ARGUMENTS

    public OutputtedFile(
            MutableDirectory parentFolder,
            String fileName,
            String outputName,
            String index,
            Optional<ManifestDescription> description) {
        super();
        this.parentFolder = parentFolder;
        this.fileName = fileName;
        this.outputName = outputName;
        this.index = index;
        this.description = description.orElse(null);
    }

    public Path calculatePath() {
        return parentFolder.calculatePath().resolve(fileName);
    }

    public Optional<ManifestDescription> getDescription() {
        return Optional.ofNullable(description);
    }
}
