/*-
 * #%L
 * anchor-test-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.test.image;

import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.test.LoggerFixture;

/**
 * A fixture for creating {@link InputOutputContext} instances for testing purposes.
 *
 * <p>This class provides utility methods to create InputOutputContext objects with different
 * configurations, such as suppressed loggers or specific model directories.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InputOutputContextFixture {

    /**
     * Creates an InputOutputContext with a suppressed logger and a specified model directory.
     *
     * @param modelDir The path to the model directory.
     * @return An InputOutputContext with a suppressed logger and the specified model directory.
     */
    public static InputOutputContext withSuppressedLogger(Path modelDir) {
        InputOutputContext out = withSuppressedLogger();
        when(out.getModelDirectory()).thenReturn(modelDir);
        return out;
    }

    /**
     * Creates an InputOutputContext with a suppressed logger.
     *
     * @return An InputOutputContext with a suppressed logger.
     */
    public static InputOutputContext withSuppressedLogger() {
        return withLogger(LoggerFixture.suppressedLogger());
    }

    /**
     * Creates an InputOutputContext with a specified logger.
     *
     * @param logger The logger to be used in the InputOutputContext.
     * @return An InputOutputContext with the specified logger.
     */
    public static InputOutputContext withLogger(Logger logger) {
        return new ContextFromLogger(logger);
    }
    
    /** Provides a partially-implemented {@link InputOutputContext} using a Logger. */
    @AllArgsConstructor
    private static class ContextFromLogger implements InputOutputContext {
    	private final Logger logger;


		@Override
		public Logger getLogger() {
			return logger;
		}

		
		@Override
		public Path getModelDirectory() {
			// Arbitrary contents, as it's assumed to be irrelvant.
			return Paths.get(".");
		}

		@Override
		public Outputter getOutputter() {
			// NEVER INTENDED TO BE CALLED
			throw new AnchorImpossibleSituationException();
		}

		@Override
		public boolean isDebugEnabled() {
			// NEVER INTENDED TO BE CALLED
			throw new AnchorImpossibleSituationException();
		}
		
		@Override
		public ExecutionTimeRecorder getExecutionTimeRecorder() {
			// NEVER INTENDED TO BE CALLED
			throw new AnchorImpossibleSituationException();
		}
    }
}
