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
/* (C)2020 */
package org.anchoranalysis.bean.xml.mock;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;

public class MockBeanNested extends AnchorBean<MockBeanNested> {

    @BeanField private String fieldSimpleNecessary;

    @BeanField @AllowEmpty private String fieldSimpleAllowEmpty = "";

    @BeanField private MockBeanSimple fieldBeanNecessary;

    @BeanField @OptionalBean private MockBeanSimple fieldBeanOptional;

    public String getFieldSimpleNecessary() {
        return fieldSimpleNecessary;
    }

    public void setFieldSimpleNecessary(String fieldSimpleNecessary) {
        this.fieldSimpleNecessary = fieldSimpleNecessary;
    }

    public String getFieldSimpleAllowEmpty() {
        return fieldSimpleAllowEmpty;
    }

    public void setFieldSimpleAllowEmpty(String fieldSimpleAllowEmpty) {
        this.fieldSimpleAllowEmpty = fieldSimpleAllowEmpty;
    }

    public MockBeanSimple getFieldBeanNecessary() {
        return fieldBeanNecessary;
    }

    public void setFieldBeanNecessary(MockBeanSimple fieldBeanNecessary) {
        this.fieldBeanNecessary = fieldBeanNecessary;
    }

    public MockBeanSimple getFieldBeanOptional() {
        return fieldBeanOptional;
    }

    public void setFieldBeanOptional(MockBeanSimple fieldBeanOptional) {
        this.fieldBeanOptional = fieldBeanOptional;
    }
}
