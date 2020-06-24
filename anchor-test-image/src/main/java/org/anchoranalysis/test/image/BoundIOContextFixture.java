package org.anchoranalysis.test.image;

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.test.LoggingFixture;

import static org.mockito.Mockito.*;

import java.nio.file.Path;

public class BoundIOContextFixture {

	private BoundIOContextFixture() {}
	
	public static BoundIOContext withSuppressedLogger() {
		return withLogger(
			LoggingFixture.suppressedLogErrorReporter()
		);
	}
	
	public static BoundIOContext withLogger(LogErrorReporter logger) {
		BoundIOContext out = mock(BoundIOContext.class);
		when(out.getLogger()).thenReturn(logger);
		when(out.getLogReporter()).thenReturn(logger.getLogReporter());
		when(out.getErrorReporter()).thenReturn(logger.getErrorReporter());
		return out;
	}
	
	public static BoundIOContext withSuppressedLogger( Path modelDir ) {
		BoundIOContext out = withSuppressedLogger();
		when(out.getModelDirectory()).thenReturn(modelDir);
		return out;
	}
}
