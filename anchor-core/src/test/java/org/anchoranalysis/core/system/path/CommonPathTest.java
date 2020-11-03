package org.anchoranalysis.core.system.path;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

/**
 * Tests {@link CommonPath}.
 * 
 * @author Owen Feehan
 *
 */
public class CommonPathTest {

    /** Two absolute-paths with commonality. */
    @Test
    public void testDirectoryAbsolutePath() throws IOException {
        assertCommonPath( "/a/b/c/d",  "/a/b", Optional.of("/a/b"));
    }

    /** Two relative-paths with commonality. */
    @Test
    public void testDirectoryRelativePath() throws IOException {
        assertCommonPath( "a/b/c/d",  "a/b", Optional.of("a/b"));
    }

    /** One absolute path and one relative-path - with therefore no commonality. */
    @Test
    public void testDirectoryAbsoluteAndRelativePath() throws IOException {
        assertCommonPath( "/a/b/c/d",  "a/b", Optional.empty());
    }
    
    private static void assertCommonPath(String path1, String path2, Optional<String> expectedCommonPath) {
        List<File> files = Arrays.asList( file(path1), file(path2) );
        assertEquals( expectedCommonPath.map(Paths::get), CommonPath.commonPath(files) );
    }
    
    private static File file(String path) {
        return new File(path);
    }
}
