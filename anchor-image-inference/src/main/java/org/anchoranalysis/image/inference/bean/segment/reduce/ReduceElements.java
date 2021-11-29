/*-
 * #%L
 * anchor-plugin-opencv
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

package org.anchoranalysis.image.inference.bean.segment.reduce;

import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.image.inference.segment.LabelledWithConfidence;
import org.anchoranalysis.image.inference.segment.WithConfidence;

/**
 * Reduces the number or spatial-extent of elements by favoring higher-confidence elements over
 * lower-confidence elements.
 *
 * @param <T> the element-type that exists in the collection (with confidence)
 * @author Owen Feehan
 */
public abstract class ReduceElements<T> extends AnchorBean<ReduceElements<T>> {

    /** A temporary label added and removed during the reduction of elements. */
    private static final String LABEL_TEMPORARY = "temporary";

    /**
     * Reduce a list of elements (each with a confidence score, <b>but no label</b>) to a
     * smaller-list.
     *
     * <p>See the class javadoc for details of algorithm.
     *
     * <p>It is not guaranteed that the resulting list will have fewer elements than the input list,
     * but never more.
     *
     * @param elements proposed bounding-boxes with scores
     * @return accepted proposals
     * @throws OperationFailedException if anything goes wrong
     */
    public List<WithConfidence<T>> reduceUnlabelled(List<WithConfidence<T>> elements)
            throws OperationFailedException {
        List<LabelledWithConfidence<T>> elementsWithLabel =
                FunctionalList.mapToList(
                        elements,
                        withConfidence ->
                                new LabelledWithConfidence<>(LABEL_TEMPORARY, withConfidence));
        List<LabelledWithConfidence<T>> reduced = reduceLabelled(elementsWithLabel);
        return FunctionalList.mapToList(reduced, LabelledWithConfidence::getWithConfidence);
    }

    /**
     * Reduce a list of elements (each with a confidence score <b>and a label</b>) to a
     * smaller-list.
     *
     * <p>It is not guaranteed that the resulting list will have fewer elements than the input list,
     * but never more.
     *
     * @param elements proposed bounding-boxes with scores
     * @return accepted proposals
     * @throws OperationFailedException if anything goes wrong
     */
    public abstract List<LabelledWithConfidence<T>> reduceLabelled(
            List<LabelledWithConfidence<T>> elements) throws OperationFailedException;
}
