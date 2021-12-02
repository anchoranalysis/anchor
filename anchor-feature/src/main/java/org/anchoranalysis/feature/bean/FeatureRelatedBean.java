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

package org.anchoranalysis.feature.bean;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.bean.initializable.InitializableBean;
import org.anchoranalysis.bean.initializable.property.AssignerMatchClass;
import org.anchoranalysis.bean.initializable.property.BeanInitializer;
import org.anchoranalysis.bean.initializable.property.ExtractDerivedParameter;
import org.anchoranalysis.bean.shared.dictionary.DictionaryInitialization;
import org.anchoranalysis.feature.initialization.FeatureRelatedInitialization;

/**
 * Beans-related to {@link Feature}s, and which require initialization with {@link
 * FeatureRelatedInitialization}.
 *
 * @author Owen Feehan
 * @param <T> bean-type
 */
public abstract class FeatureRelatedBean<T>
        extends InitializableBean<T, FeatureRelatedInitialization> {

    /** Create with default initializers. */
    protected FeatureRelatedBean() {
        super(
                new BeanInitializer<>(FeatureRelatedInitialization.class, paramExtracters()),
                new AssignerMatchClass<FeatureRelatedInitialization>(
                        FeatureRelatedInitialization.class));
    }

    private static List<ExtractDerivedParameter<FeatureRelatedInitialization, ?>>
            paramExtracters() {
        return Arrays.asList(
                new ExtractDerivedParameter<>(
                        DictionaryInitialization.class,
                        FeatureRelatedInitialization::getDictionary));
    }
}
