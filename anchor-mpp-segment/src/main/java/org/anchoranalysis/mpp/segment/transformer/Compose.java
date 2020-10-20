/*-
 * #%L
 * anchor-mpp-sgmn
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

package org.anchoranalysis.mpp.segment.transformer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * Combines a transformation from {@code S} to {@code U} with one from {@code U} to {@code T}
 *
 * @author Owen Feehan
 * @param <S> source type
 * @param <T> destination type
 * @param <U> intermediate type
 */
@NoArgsConstructor
@AllArgsConstructor
public class Compose<S, T, U> extends StateTransformerBean<S, T> {

    // START BEAN PROPERTIES
    /** First transformation from {@code S} to {@code U} */
    @BeanField @Getter @Setter private StateTransformerBean<S, U> first;

    /** Second transformation from {@code U} to {@code T} */
    @BeanField @Getter @Setter private StateTransformerBean<U, T> second;
    // END BEAN PROPERTIES

    @Override
    public T transform(S in, TransformationContext context) throws OperationFailedException {

        U inter = first.transform(in, context);

        return second.transform(inter, context);
    }
}
