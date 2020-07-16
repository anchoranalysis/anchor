/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.bundle;

import java.io.Serializable;

public class BundleItem<T extends Serializable> implements Serializable {
    /** */
    private static final long serialVersionUID = 2630085442934070697L;

    private String index;
    private T object;

    public BundleItem(String index, T object) {
        super();
        this.index = index;
        this.object = object;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {

        this.object = object;
    }
}
