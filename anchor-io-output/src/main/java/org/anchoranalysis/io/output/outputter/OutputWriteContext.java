package org.anchoranalysis.io.output.outputter;

import java.util.Optional;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Settings and user-arguments for writing files.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class OutputWriteContext {

    /** User-define settings for outputting in output-manager. */
    @Getter private OutputWriteSettings settings;
    
    /** A suggestion on what file-format to write. */
    @Getter private Optional<ImageFileFormat> suggestedFormatToWrite;
    
    public OutputWriteContext() {
        settings = new OutputWriteSettings();
    }
}
