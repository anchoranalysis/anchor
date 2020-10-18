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
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * A description of an entry (e.g. a file or directory) in the manifest.
 *
 * <p>This is an <i>immutable</i> type.
 *
 * <p>{@code function} is intended to further specify {@code type} as there may be many entities of
 * identical type.
 *
 * <p>Together {@code type} and {@code function} should serve as a unique identifier (like a joint
 * primary key).
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@Value
public class ManifestDescription implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    /** Identifies the type of entity. */
    private final String type;

    /** Identifies the function the entity plays. */
    private final String function;

    /**
     * Assigns a new function.
     *
     * @param functionToAssign the function to assign
     * @return a newly copied object with a changed function and otherwise unchanged.
     */
    public ManifestDescription assignFunction(String functionToAssign) {
        return new ManifestDescription(type, functionToAssign);
    }

    /**
     * Assigns a new type.
     *
     * @param typeToAssign the type to assign
     * @return a newly copied object with a changed type and otherwise unchanged.
     */
    public ManifestDescription assignType(String typeToAssign) {
        return new ManifestDescription(typeToAssign, function);
    }
}
