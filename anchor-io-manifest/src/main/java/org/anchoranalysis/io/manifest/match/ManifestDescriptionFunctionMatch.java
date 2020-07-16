/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import org.anchoranalysis.io.manifest.ManifestDescription;

public class ManifestDescriptionFunctionMatch implements Match<ManifestDescription> {

    private String function;

    public ManifestDescriptionFunctionMatch(String function) {
        super();
        this.function = function;
    }

    @Override
    public boolean matches(ManifestDescription obj) {
        return obj.getFunction().equals(function);
    }
}
