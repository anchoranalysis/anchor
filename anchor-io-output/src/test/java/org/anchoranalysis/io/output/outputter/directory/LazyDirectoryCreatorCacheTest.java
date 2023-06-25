/*-
 * #%L
 * anchor-io-output
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
package org.anchoranalysis.io.output.outputter.directory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.io.output.outputter.DirectoryCreationParameters;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests that the correct directories are created for particular types of paths.
 *
 * @author Owen Feehan
 */
class LazyDirectoryCreatorCacheTest {

    /** The directory in which the test creates files. */
	@TempDir Path directory;

    private static final String RELATIVE_PATH_DIRECT = "path1";
    private static final String RELATIVE_PATH_NESTED = "path1/nested1";

    private LazyDirectoryCreatorPool directoryCreator;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setup() {
        directoryCreator =
                new LazyDirectoryCreatorPool(directory, new DirectoryCreationParameters());
    }

    /**
     * Tests a child element that is a direct descendant.
     * 
     * @throws GetOperationFailedException if the exception throws it.
     */
    @Test
    void testDirectChild() throws GetOperationFailedException {
        Path path = pathFor(RELATIVE_PATH_DIRECT);
        executeAndCheckBefore(path, true);
        assertPathValid(path);
    }

    /**
     * Tests a child element that is a non-direct descendant.
     * 
     * @throws GetOperationFailedException if the exception throws it.
     */
    @Test
    void testNestedChild() throws GetOperationFailedException {
        Path pathDirect = pathFor(RELATIVE_PATH_DIRECT);
        Path pathNested = pathFor(RELATIVE_PATH_NESTED);
        executeAndCheckBefore(pathNested, true);
        assertPathValid(pathDirect);
        assertPathValid(pathNested);

        // Execute on the direct path, and check its not called again
        executeAndCheckBefore(pathDirect, false);
    }

    /**
     * Executes an operation on a particular path with a <i>before</i> optations, and checks before
     * has been called
     *
     * @throws GetOperationFailedException
     */
    private void executeAndCheckBefore(Path path, boolean expectedIsCalled)
            throws GetOperationFailedException {
        OneTimeOperation before = new OneTimeOperation();
        WriterExecuteBeforeEveryOperation op =
                directoryCreator.getOrCreate(path, Optional.of(before));
        op.execute();
        assertEquals(expectedIsCalled, before.isCalled(), "isCalled");
    }

    /**
     * Asserts that a path exists as a directory, and has been initialized
     *
     * @throws GetOperationFailedException
     */
    private void assertPathValid(Path path) throws GetOperationFailedException {
        Optional<LazyDirectoryCreator> creator = directoryCreator.get(path);
        assertTrue(creator.isPresent(), "creator exists in cache");
        assertTrue(creator.get().isInitialized(), "isInitialized");
        assertTrue(path.toFile().exists(), "exists");
        assertTrue(path.toFile().isDirectory(), "isDirectory");
    }

    /** Constructs a path in the temporary-folder */
    private Path pathFor(String relativePath) {
        return directory.resolve(relativePath);
    }
}
