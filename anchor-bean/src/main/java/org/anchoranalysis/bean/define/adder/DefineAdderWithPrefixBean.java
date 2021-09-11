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

package org.anchoranalysis.bean.define.adder;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.define.DefineAddException;

/**
 * Like {@link DefineAdderBean} but adds a prefix to the name of beans when adding.
 *
 * @author Owen Feehan
 */
public abstract class DefineAdderWithPrefixBean extends DefineAdderBean {

    // START BEAN PROPERTIES
    /** A prefix that is placed before the name of every bean created */
    @BeanField @AllowEmpty @Getter @Setter private String prefix = "";
    // END BEAN PROPERTIES

    /**
     * Add an item to {@code define} with a name being added.
     *
     * @param define the define.
     * @param name the name of the bean.
     * @param item the item to add.
     * @throws DefineAddException if a {#link GroupingRoot} cannot be found in the class-hierarchy.
     */
    protected void addWithName(Define define, String name, AnchorBean<?> item)
            throws DefineAddException {
        NamedBean<?> bean = new NamedBean<>(resolveName(name), item);
        define.add(bean);
    }

    /**
     * Prepends a prefix before the name.
     *
     * @param name the name to prepend.
     * @return the name with a prefix prepended.
     */
    protected String resolveName(String name) {
        return prefix + name;
    }
}
