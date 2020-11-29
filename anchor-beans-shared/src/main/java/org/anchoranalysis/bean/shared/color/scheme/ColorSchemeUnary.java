/*-
 * #%L
 * anchor-beans-shared
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
package org.anchoranalysis.bean.shared.color.scheme;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * Like {@link ColorScheme} but employs a unary operator on a call to an existing {@link
 * ColorScheme}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class ColorSchemeUnary extends ColorScheme {

    // START BEAN PROPERTIES
    /** The delegate that creates the (unshuffled) list. */
    @BeanField @Getter @Setter private ColorScheme colors;
    // END BEAN PROPERTIES

    @Override
    public ColorList createList(int size) throws OperationFailedException {
        ColorList list = colors.createList(size);
        transform(list);
        return list;
    }

    /**
     * Transform an existing {@link ColorList}.
     *
     * <p>Note that the incoming {@link ColorList} may be modified, and can no longer be used in its
     * original form after this method call.
     *
     * @param source the incoming {@link ColorList} (which may be consumed and modified).
     * @return the transformed (outgoing) {@link ColorList}.
     */
    protected abstract ColorList transform(ColorList source);
}
