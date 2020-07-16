/*-
 * #%L
 * anchor-io-manifest
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
package org.anchoranalysis.io.manifest.deserializer.folder;

import java.io.Serializable;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.manifest.deserializer.bundle.Bundle;
import org.anchoranalysis.io.manifest.deserializer.bundle.BundleParameters;

public class BundleDeserializers<T extends Serializable> {
    private Deserializer<Bundle<T>> deserializerBundle;
    private Deserializer<BundleParameters> deserializerBundleParameters;

    public BundleDeserializers(
            Deserializer<Bundle<T>> deserializerBundle,
            Deserializer<BundleParameters> deserializerBundleParameters) {
        super();
        this.deserializerBundle = deserializerBundle;
        this.deserializerBundleParameters = deserializerBundleParameters;
    }

    public Deserializer<Bundle<T>> getDeserializerBundle() {
        return deserializerBundle;
    }

    public Deserializer<BundleParameters> getDeserializerBundleParameters() {
        return deserializerBundleParameters;
    }
}
