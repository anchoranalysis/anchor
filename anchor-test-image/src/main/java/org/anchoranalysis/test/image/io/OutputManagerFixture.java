package org.anchoranalysis.test.image.io;

import java.nio.file.Path;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class OutputManagerFixture {
    
    public static OutputManager createOutputManager(Path pathForPrefixer) {
        OutputManager outputManager = new OutputManager();
        outputManager.setSilentlyDeleteExisting(true);
        outputManager.setOutputWriteSettings( settings() );
        outputManager.setFilePathPrefixer(new FilePathPrefixerConstantPath(pathForPrefixer));
        return outputManager;
    }
    
    private static OutputWriteSettings settings() {

        TestReaderWriterUtilities.ensureRasterWriter();
        
        OutputWriteSettings settings = new OutputWriteSettings();

        // We populate any defaults in OutputWriteSettings from our default bean factory
        try {
            settings.checkMisconfigured(RegisterBeanFactories.getDefaultInstances());
        } catch (BeanMisconfiguredException e1) {
            throw new AnchorFriendlyRuntimeException(e1);
        }
        
        return settings;
    }
}
