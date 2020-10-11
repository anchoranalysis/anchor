package org.anchoranalysis.io.generator.sequence.pattern;

import java.util.Optional;
import org.anchoranalysis.core.functional.OptionalUtilities;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class OutputPatternUtilities {
    
    public static Optional<String> maybeSubdirectory(String outputName, boolean suppressSubdirectory) {
        return OptionalUtilities.createFromFlag(!suppressSubdirectory, outputName);
    }
}
