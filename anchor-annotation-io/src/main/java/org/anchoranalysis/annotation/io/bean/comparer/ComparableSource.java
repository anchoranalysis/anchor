/*-
 * #%L
 * anchor-annotation-io
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

package org.anchoranalysis.annotation.io.bean.comparer;

import java.nio.file.Path;
import org.anchoranalysis.annotation.io.image.findable.Findable;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.input.InputReadFailedException;

/**
 * A set of elements, loaded from the file-system, to be compared to another set.
 *
 * <p>The source may or not be a {@link ObjectCollection}, but it is converted into a {@link
 * ObjectCollection} to be compared to another set (as a common basis for comparison between
 * different source types).
 *
 * @author Owen Feehan
 */
public abstract class ComparableSource extends AnchorBean<ComparableSource> {

    /**
     * Loads the source of elements from the file-system and converts to a {@link ObjectCollection}.
     *
     * <p>The location of elements on the file-system may be derived from a {@code reference} path.
     *
     * @param reference the source file-path used to help identify where elements are located on the
     *     file-system.
     * @param dimensions how large the scene is, in which elements are being compared. This is
     *     usually the same as the image-size.
     * @param debugMode true if debug-mode is activated, which can influence paths on the
     *     file-system.
     * @param context records the execution time of certain operations.
     * @return the elements converted into a {@link ObjectCollection} and wrapped into a {@link
     *     Findable} element that indicates if they were successfully found on the file-system.
     * @throws InputReadFailedException if the objects cannot be successfully loaded.
     */
    public abstract Findable<ObjectCollection> loadAsObjects(
            Path reference, Dimensions dimensions, boolean debugMode, OperationContext context)
            throws InputReadFailedException;
}
