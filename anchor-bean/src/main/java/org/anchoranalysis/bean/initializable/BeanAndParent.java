/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.initializable;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor @Value
class BeanAndParent {

    /** The bean */
    private AnchorBean<?> bean;

    /** Parent bean, or null if the bean doesn't have a parent */
    private BeanAndParent parent;

    public AnchorBean<?> parentBean() {
        return parent != null ? parent.getBean() : null;
    }

    /**
     * Path from the the root of the tree (null parent) to the current item expressed as a string
     * with ({@literal "->"} as a divider
     *
     * <p>{@literal From root->leaf = left->right}
     *
     * @return a string showing the path with {@literal "->"} as a divider
     */
    public String pathFromRootAsString() {
        // Populate list from root to leaf with the beans along the path, and convert to string
        return stringFromPath(beansRootToLeaf());
    }

    private List<AnchorBean<?>> beansRootToLeaf() {

        List<AnchorBean<?>> listObjects = new ArrayList<>();

        BeanAndParent next = this;
        listObjects.add(0, next.getBean());
        while ((next = next.getParent()) != null) {
            listObjects.add(0, next.getBean());
        }

        return listObjects;
    }

    private static String stringFromPath(List<AnchorBean<?>> listObjects) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listObjects.size(); i++) {

            if (i != 0) {
                sb.append("->");
            }

            AnchorBean<?> beanIter = listObjects.get(i);
            sb.append(beanIter.getBeanName());
        }
        return sb.toString();
    }
}
