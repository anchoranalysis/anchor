/* (C)2020 */
package org.anchoranalysis.io.manifest.file;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.io.manifest.ManifestDescription;

@Value
@AllArgsConstructor
public class FileType implements Serializable {

    /** */
    private static final long serialVersionUID = 1822279428811663437L;

    private ManifestDescription manifestDescription;
    private String fileExtension;
}
