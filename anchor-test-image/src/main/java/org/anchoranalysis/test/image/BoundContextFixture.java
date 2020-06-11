package org.anchoranalysis.test.image;

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.test.LoggingFixture;

import static org.mockito.Mockito.*;

import java.nio.file.Path;

public class BoundContextFixture {

	private BoundContextFixture() {}
	
	public static BoundIOContext withSimpleLogger() {
		LogErrorReporter logger = LoggingFixture.simpleLogErrorReporter();
		
		BoundIOContext out = mock(BoundIOContext.class);
		when(out.getLogger()).thenReturn(logger);
		when(out.getLogReporter()).thenReturn(logger.getLogReporter());
		when(out.getErrorReporter()).thenReturn(logger.getErrorReporter());
		return out;
	}
	
	public static BoundIOContext withSimpleLogger( Path modelDir ) {
		BoundIOContext out = withSimpleLogger();
		when(out.getModelDirectory()).thenReturn(modelDir);
		return out;
	}
}
