/* (C)2020 */
package org.anchoranalysis.io.manifest;

import java.io.Serializable;

public class ManifestDescription implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    private final String type;
    private final String function;

    public ManifestDescription(String type, String function) {
        super();
        this.type = type;
        this.function = function;
        assert (!this.function.isEmpty());
    }

    public String getFunction() {
        return function;
    }

    public String getType() {
        return type;
    }
}
