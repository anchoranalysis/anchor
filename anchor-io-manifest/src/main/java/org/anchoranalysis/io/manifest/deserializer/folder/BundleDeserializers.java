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
