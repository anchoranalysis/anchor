package org.anchoranalysis.core.system.path;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class CommonPathTest {

    @Test
    public void testDirectoryAbsoluteWindowsDirectory() throws IOException {
        List<File> files = Arrays.asList( file("/a/b/c/d"), file("/a/b") );
        assertEquals("/a/b", CommonPath.commonPath(files) );
    }
    
    private static File file(String path) {
        return new File(path);
    }
}
