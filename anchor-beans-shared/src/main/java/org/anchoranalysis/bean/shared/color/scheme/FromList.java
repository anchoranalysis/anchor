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

package org.anchoranalysis.bean.shared.color.scheme;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.exception.BeanDuplicateException;
import org.anchoranalysis.bean.shared.color.RGBColorBean;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * Specifies a list of colors by a list.
 *
 * <p>If the list is too small for size, then it is extended by repeating the ultimate item.
 *
 * @author Owen Feehan
 */
public class FromList extends ColorScheme {

    // START BEAN PROPERTIES
    /** The list of colors to specify */
    @BeanField @Getter @Setter private List<RGBColorBean> colors;
    // END BEAN PROPERTIES

    @Override
    public ColorList createList(int size) throws OperationFailedException {

        try {
            ColorList out = new ColorList();

            for (RGBColorBean item : colors) {
                addDuplicate(out, item);
            }

            while (out.size() < size) {
                addDuplicate(out, colors.get(colors.size() - 1));
            }

            return out;

        } catch (BeanDuplicateException e) {
            throw new OperationFailedException(e);
        }
    }

    private void addDuplicate(ColorList out, RGBColorBean bean) {
        out.add(bean.rgbColor().duplicate());
    }
}
