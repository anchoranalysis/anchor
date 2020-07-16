/* (C)2020 */
package org.anchoranalysis.io.generator.serialized;

import java.nio.file.Path;
import org.anchoranalysis.io.generator.SingleFileTypeGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public abstract class SerializedGenerator extends SingleFileTypeGenerator {

    @Override
    public abstract void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException;
}
