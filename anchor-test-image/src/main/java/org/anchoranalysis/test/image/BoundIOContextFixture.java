/* (C)2020 */
package org.anchoranalysis.test.image;

import static org.mockito.Mockito.*;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.test.LoggingFixture;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoundIOContextFixture {

    public static BoundIOContext withSuppressedLogger(Path modelDir) {
        BoundIOContext out = withSuppressedLogger();
        when(out.getModelDirectory()).thenReturn(modelDir);
        return out;
    }

    public static BoundIOContext withSuppressedLogger() {
        return withLogger(LoggingFixture.suppressedLogErrorReporter());
    }

    public static BoundIOContext withLogger(Logger logger) {
        BoundIOContext out = spy(BoundIOContext.class);
        when(out.getLogger()).thenReturn(logger);
        when(out.getLogReporter()).thenReturn(logger.messageLogger());
        when(out.getErrorReporter()).thenReturn(logger.errorReporter());
        return out;
    }
}
