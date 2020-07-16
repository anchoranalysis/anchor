/* (C)2020 */
package org.anchoranalysis.io.manifest;

import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

@RequiredArgsConstructor
public class ManifestFolderDescription implements Serializable {

    /** */
    private static final long serialVersionUID = -9161070529853431830L;

    // START REQUIRED ARGUMENTS
    @Getter private final ManifestDescription fileDescription;

    @Getter private final SequenceType sequenceType;
    // END REQUIRED ARGUMENTS

    public ManifestFolderDescription(String type, String function, SequenceType sequenceType) {
        this.fileDescription = new ManifestDescription(type, function);
        this.sequenceType = sequenceType;
    }
}
