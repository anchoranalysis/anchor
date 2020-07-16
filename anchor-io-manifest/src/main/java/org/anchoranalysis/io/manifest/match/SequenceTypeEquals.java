/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

public class SequenceTypeEquals implements Match<SequenceType> {

    private String name;

    public SequenceTypeEquals(String name) {
        super();
        this.name = name;
    }

    @Override
    public boolean matches(SequenceType obj) {
        return obj.getName().equals(name);
    }
}
