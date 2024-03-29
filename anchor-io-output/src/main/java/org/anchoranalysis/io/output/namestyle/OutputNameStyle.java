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

package org.anchoranalysis.io.output.namestyle;

import java.io.Serializable;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Base class for an approach to generate a filename for an output.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@NoArgsConstructor
public abstract class OutputNameStyle implements Serializable {

    private static final long serialVersionUID = 1L;

    /** An identifier used in rules to determine if an output is enabled or not. */
    @Getter @Setter private String outputName;

    /**
     * The filename to be written, including prefix, suffix, etc. but <b>excluding</b> extension
     * (and any period before the extension).
     *
     * @return the filename (without an extension, including without the period before the
     *     extension), if one is defined. If one is not defined, the output is expected to occur
     *     without any filename.
     */
    public abstract Optional<String> filenameWithoutExtension();

    /**
     * Deep copy the current object.
     *
     * @return a newly created copy.
     */
    public abstract OutputNameStyle duplicate();
}
