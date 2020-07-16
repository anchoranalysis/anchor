/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import java.util.ArrayList;
import org.anchoranalysis.io.manifest.ManifestDescription;

public class ManifestDescriptionMatchOr implements Match<ManifestDescription> {

    private ArrayList<Match<ManifestDescription>> list = new ArrayList<>();

    public ManifestDescriptionMatchOr() {}

    public ManifestDescriptionMatchOr(
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

            if (item.matches(obj)) {
                return true;
            }
        }
        return false;
    }
}
