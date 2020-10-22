package org.anchoranalysis.test.io.output;

import java.util.Optional;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.path.prefixer.FilePathPrefixerContext;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerException;
import org.anchoranalysis.io.output.recorded.RecordedOutputsWithRules;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class OutputterCheckedFixture {

    public static OutputterChecked create(Manifest manifest) throws BindFailedException {
        return createFrom(
           OutputManagerFixture.createOutputManager(Optional.empty()),
           manifest
        );
    }
    
    public static OutputterChecked createFrom(OutputManager outputManager) throws BindFailedException {
        return createFrom(outputManager, new Manifest());
    }
    
    public static OutputterChecked createFrom(OutputManager outputManager, Manifest manifest)
            throws BindFailedException {
        try {
            return outputManager.createExperimentOutputter(
                    "debug",
                    manifest,
                    new RecordedOutputsWithRules(),
                    new FilePathPrefixerContext(false, Optional.empty()));
        } catch (PathPrefixerException e) {
            throw new BindFailedException(e);
        }
    }
}
