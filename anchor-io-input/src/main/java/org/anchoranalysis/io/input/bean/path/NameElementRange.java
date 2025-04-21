/*-
 * #%L
 * anchor-io-input
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.input.bean.path;

import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.index.range.IndexRangeNegative;
import org.anchoranalysis.io.input.path.DerivePathException;
import org.anchoranalysis.io.input.path.ExtractPathElementRange;

/**
 * Constructs a {@link Path} from a sub-range of the name-elements of the {@link Path}.
 *
 * <p>The name-elements are a split of the {@link Path} by the directory-separator. See the {@link
 * Path} Javadoc.
 *
 * <p>All indices begin at 0 (for the first element), and can also accept negative-indices which
 * count backwards from the end.
 *
 * <p>e.g. -1 is the last element; -2 is the second-last element.
 *
 * @author Owen Feehan
 */
public class NameElementRange extends DerivePath {

    // START BEAN PROPERTIES
    /**
     * The index of the first element (inclusive) for the range.
     *
     * <p>Zero-indexed. It can be negative, in which it counts backwards from the end. See class
     * description.
     */
    @BeanField @Getter @Setter private int indexStart = 0;

    /**
     * The index of the last element (inclusive) for the range.
     *
     * <p>Zero-indexed. It can be negative, in which it counts backwards from the end. See class
     * description.
     */
    @BeanField @Getter @Setter private int indexEnd = -2;

    // END BEAN PROPERTIES

    private IndexRangeNegative indexRange;

    @Override
    public void checkMisconfigured(BeanInstanceMap defaultInstances)
            throws BeanMisconfiguredException {
        super.checkMisconfigured(defaultInstances);
        try {
            this.indexRange = new IndexRangeNegative(indexStart, indexEnd);
        } catch (OperationFailedException e) {
            throw new BeanMisconfiguredException("The indices are not correctly ordered", e);
        }
    }

    @Override
    public Path deriveFrom(Path source, boolean debugMode) throws DerivePathException {
        return ExtractPathElementRange.extract(source, indexRange);
    }
}
