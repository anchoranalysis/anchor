/* (C)2020 */
package org.anchoranalysis.feature.nrg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.anchoranalysis.core.params.KeyValueParams;

// Parameters derived from a particular image
public class NRGElemParamsFromImage implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    private Map<String, Double> params;

    public NRGElemParamsFromImage() {
        params = new HashMap<>();
    }

    public boolean containsKey(String key) {
        return params.containsKey(key);
    }

    public double get(String key) {
        return params.get(key);
    }

    public void put(String key, Double value) {
        params.put(key, value);
    }

    public KeyValueParams createKeyValueParams() {
        KeyValueParams out = new KeyValueParams();
        for (Entry<String, Double> entry : params.entrySet()) {
            out.put(entry.getKey(), entry.getValue());
        }
        return out;
    }
}
