package org.anchoranalysis.core.system.path;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class CommonPathTest {

    @Test
    public void testDirectoryAbsoluteWindowsDirectory() throws IOException {
        List<File> files = Arrays.asList( file("/a/b/c/d"), file("/a/b") );
        assertEquals( Optional.of(Paths.get("/a/b")), CommonPath.commonPath(files) );
    }
    
    private static File file(String path) {
        return new File(path);
    }
}
