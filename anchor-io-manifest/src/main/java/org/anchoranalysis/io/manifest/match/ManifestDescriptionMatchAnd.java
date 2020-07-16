/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import java.util.ArrayList;
import org.anchoranalysis.io.manifest.ManifestDescription;

public class ManifestDescriptionMatchAnd implements Match<ManifestDescription> {

    private ArrayList<Match<ManifestDescription>> list = new ArrayList<>();

    public ManifestDescriptionMatchAnd() {}

    public ManifestDescriptionMatchAnd(
            Match<ManifestDescription> condition1, Match<ManifestDescription> condition2) {
        list.add(condition1);
        list.add(condition2);
    }

    public void addCondition(Match<ManifestDescription> condition) {
        list.add(condition);
    }

    @Override
    public boolean matches(ManifestDescription obj) {

        for (Match<ManifestDescription> item : list) {

            if (!item.matches(obj)) {
                return false;
            }
        }
        return true;
    }
}
