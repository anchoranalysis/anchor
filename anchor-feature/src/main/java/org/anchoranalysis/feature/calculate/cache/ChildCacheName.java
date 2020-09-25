/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.calculate.cache;

import lombok.EqualsAndHashCode;

/**
 * A unique identifier for a child-cache name, that uses a class and optionally additionally a
 * part-name
 *
 * <ul>
 *   <li>a class: guaranteed to be unique for the class
 *   <li>an optional part-name (string): a further division of the class into different caches
 * </ul>
 */
@EqualsAndHashCode
public class ChildCacheName {

    private Class<?> cls;
    private String part;

    /**
     * Uses only the class as an identifier - and a blank part-name
     *
     * @param cls class
     */
    public ChildCacheName(Class<?> cls) {
        this(cls, "");
    }

    /**
     * Uses only the class as an identifier - and a integer part-name
     *
     * @param cls class
     */
    public ChildCacheName(Class<?> cls, int id) {
        this(cls, String.valueOf(id));
    }

    /**
     * Uses both the class and a part-name as an identifier
     *
     * @param cls class
     * @param part part-name
     */
    public ChildCacheName(Class<?> cls, String part) {
        super();
        this.cls = cls;
        this.part = part;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", cls.getCanonicalName(), part);
    }
}
