/* (C)2020 */
package org.anchoranalysis.io.generator;

import java.util.Optional;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.writer.WritableItem;

public interface Generator extends WritableItem {

    Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings);
}
