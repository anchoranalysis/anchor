/* (C)2020 */
package org.anchoranalysis.io.bean.filepath.generator;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.error.AnchorIOException;

/** Always generates a constant path irrespective of the the input */
@NoArgsConstructor
@AllArgsConstructor
public class FilePathGeneratorConstant extends FilePathGenerator {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String value;
    // END BEAN PROPERTIES

    @Override
    public Path outFilePath(Path pathIn, boolean debugMode) throws AnchorIOException {
        return Paths.get(value);
    }
}
