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

package org.anchoranalysis.annotation.io.bean;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.StringMap;
import org.anchoranalysis.image.core.stack.Stack;

/**
 * How to display one or more {@link Stack}s as background during an annotation process.
 *
 * @author Owen Feehan
 */
public class AnnotationBackground extends AnchorBean<AnnotationBackground> {

    // START BEAN PROPERTIES
    /**
     * The name of the default stack from {@code backgroundStackMap} to display as background when
     * annotating.
     */
    @BeanField @Getter @Setter private String stackNameDefault;

    /** If non-null, maps underlying stack-name to a background. */
    @BeanField @OptionalBean @Getter @Setter private StringMap backgroundStackMap;

    /**
     * If non-empty, to not display any stacks in {@code backgroundStackMap} whose name contains
     * this string.
     */
    @BeanField @AllowEmpty @Getter @Setter private String ignoreContains = "";
    // END BEAN PROPERTIES
}
