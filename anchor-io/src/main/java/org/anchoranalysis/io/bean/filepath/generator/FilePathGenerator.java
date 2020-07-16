/* (C)2020 */
package org.anchoranalysis.io.bean.filepath.generator;

import java.nio.file.Path;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.io.error.AnchorIOException;

public abstract class FilePathGenerator extends AnchorBean<FilePathGenerator> {

    // Give a string that is prefixed to all output files, to give the output file path
    public abstract Path outFilePath(Path pathIn, boolean debugMode) throws AnchorIOException;
}
