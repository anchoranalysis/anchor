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
package org.anchoranalysis.io.input.grouper;

import java.nio.file.Path;
import org.anchoranalysis.io.input.path.DerivePathException;

/**
 * Derives a grouping key from an input.
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface InputGrouper {

    /**
     * Derives a key for the group from {@code identifier}.
     *
     * <p>This key determines which group {@code input} belongs to e.g. like a GROUP BY key in
     * databases.
     *
     * @param identifier an identifier for an input, expressed as a {@link Path}.
     * @return a derived grouping-key.
     * @throws DerivePathException if a key cannot be derived from {@code identifier} successfully.
     */
    String deriveGroupKeyOptional(Path identifier) throws DerivePathException;
}
