package org.anchoranalysis.io.output.bound;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests that the correct directories are created for particular types of paths.
 * 
 * @author Owen Feehan
 *
 */
public class LazyDirectoryCreatorCacheTest {

    @Rule public TemporaryFolder folder = new TemporaryFolder();
    
    private static final String RELATIVE_PATH_DIRECT = "path1";
    private static final String RELATIVE_PATH_NESTED = "path1/nested1";
    
    private LazyDirectoryCreatorCache directoryCreator;
    
    @Before
    public void setup() {
        directoryCreator = new LazyDirectoryCreatorCache(folder.getRoot().toPath(), false);
    }
    
    @Test
    public void testDirectChild() throws GetOperationFailedException {
        Path path = pathFor(RELATIVE_PATH_DIRECT);
        executeAndCheckBefore(path, true);
        assertPathValid(path);
    }
    
    @Test
    public void testNestedChild() throws GetOperationFailedException {
        Path pathDirect = pathFor(RELATIVE_PATH_DIRECT);
        Path pathNested = pathFor(RELATIVE_PATH_NESTED);
        executeAndCheckBefore(pathNested, true);
        assertPathValid(pathDirect);
        assertPathValid(pathNested);
        
        // Execute on the direct path, and check its not called again
        executeAndCheckBefore(pathDirect, false);
    }
    
    /** Executes an operation on a particular path with a <i>before</i> optations, and checks before has been called 
     * @throws GetOperationFailedException */ 
    private void executeAndCheckBefore(Path path, boolean expectedIsCalled) throws GetOperationFailedException {
        OneTimeOperation before = new OneTimeOperation();
        WriterExecuteBeforeEveryOperation op = directoryCreator.getOrCreate(path, Optional.of(before));
        op.execute();
        assertEquals("isCalled", expectedIsCalled, before.isCalled() );        
    }
    
    /** Asserts that a path exists as a directory, and has been initialized 
     * @throws GetOperationFailedException */
    private void assertPathValid(Path path) throws GetOperationFailedException {
        Optional<LazyDirectoryCreator> creator = directoryCreator.get(path);
        assertTrue("creator exists in cache", creator.isPresent() );
        assertTrue("isInitialized", creator.get().isInitialized() );
        assertTrue("exists", path.toFile().exists() );
        assertTrue("isDirectory", path.toFile().isDirectory() );        
    }
    
    /** Constructs a path in the temporary-folder */
    private Path pathFor(String relativePath) {
        return folder.getRoot().toPath().resolve(relativePath);
    }
}
