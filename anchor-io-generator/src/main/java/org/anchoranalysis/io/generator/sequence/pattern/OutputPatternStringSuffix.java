package org.anchoranalysis.io.generator.sequence.pattern;

import java.util.Optional;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.io.namestyle.StringSuffixOutputNameStyle;

/**
 * A {@link OutputPattern} that outputs a file-name with a trailing string index.
 * 
 * @author Owen Feehan
 *
 */
public class OutputPatternStringSuffix extends OutputPattern {

    public OutputPatternStringSuffix(String outputName, boolean supressSubdirectory) {
        this(outputName, supressSubdirectory, "");
    }
    
    public OutputPatternStringSuffix(String outputName, boolean supressSubdirectory, String prefix) {
        super(
           maybeSubdirectory(outputName, supressSubdirectory),
           new StringSuffixOutputNameStyle(outputName, prefix + "%s"),
           true,
           Optional.empty()
        );
    }
    
    private static Optional<String> maybeSubdirectory(String outputName, boolean supressSubdirectory) {
        return OptionalUtilities.createFromFlag(!supressSubdirectory, outputName);
    }
}
