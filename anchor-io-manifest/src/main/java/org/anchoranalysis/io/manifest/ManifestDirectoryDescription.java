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

package org.anchoranalysis.io.manifest;

import java.io.Serializable;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Getter;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

/**
 * A description of an directory in the manifest.
 *
 * <p>This is an <i>immutable</i> type.
 *
 * <p>It combines a {@link ManifestDescription} as used for a file, together with a {@link
 * SequenceType} to describe any sequence that might exist.
 *
 * @author Owen Feehan
 */
public class ManifestDirectoryDescription implements Serializable {

    /** */
    private static final long serialVersionUID = -9161070529853431830L;

    // START REQUIRED ARGUMENTS
    /** 
     * The description of the contents of the directory.
     *
     * <p>This is stored as a {@link Nullable} rather than a {@link Optional} to allow serialization.
     **/
    @Nullable private final ManifestDescription description;

    /** A sequence-type to describe the different filenames outputted in the directory. */
    @Getter private final SequenceType<?> sequenceType;
    // END REQUIRED ARGUMENTS
    
    public ManifestDirectoryDescription(Optional<ManifestDescription> description, SequenceType<?> sequenceType) {
        this.description = description.orElse(null);
        this.sequenceType = sequenceType;
    }

    public ManifestDirectoryDescription(
            String type, String function, SequenceType<?> sequenceType) {
        this.description = new ManifestDescription(type, function);
        this.sequenceType = sequenceType;
    }

    public Optional<ManifestDescription> getDescription() {
        return Optional.ofNullable(description);
    }
}
