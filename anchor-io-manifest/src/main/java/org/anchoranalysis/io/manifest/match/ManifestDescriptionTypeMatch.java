/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import org.anchoranalysis.io.manifest.ManifestDescription;

public class ManifestDescriptionTypeMatch implements Match<ManifestDescription> {

    private String type;

    public ManifestDescriptionTypeMatch(String type) {
        super();
        this.type = type;
    }

    @Override
    public boolean matches(ManifestDescription obj) {
        return obj.getType().equals(type);
    }
}
