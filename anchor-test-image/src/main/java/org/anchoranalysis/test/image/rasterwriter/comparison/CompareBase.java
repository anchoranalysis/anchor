package org.anchoranalysis.test.image.rasterwriter.comparison;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.nio.file.Path;
import org.anchoranalysis.test.image.DualComparer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract class CompareBase implements ImageComparer {

    protected DualComparer comparer;
    
    /**
     * Asserts that two stacks being compared are identical.
     *
     * @param filenameWithoutExtension the filename before extensions were added.
     * @param filenameWithExtension the filename with an extension added.
     * @param path a path to be displayed if an error occurs. 
     * @throws IOException
     */
    public void assertIdentical(
        String filenameWithoutExtension,
        String filenameWithExtension,
        Path path
       ) throws IOException {
        
        try {
            String assertMessage = filenameWithoutExtension + "_" + identifierForTest();
            assertTrue(assertMessage, areIdentical(filenameWithoutExtension, filenameWithExtension));
        } catch (IOException e) {
            System.err.printf( // NOSONAR
                    "The test wrote a file to temporary-folder directory at:%n%s%n", path);
            throw new IOException(
                    String.format(
                            "The comparer threw an IOException, which likely means it cannot find an appropriate raster to compare against for %s.",
                            filenameWithoutExtension),
                    e);
        }
    }
    
    protected abstract boolean areIdentical(String filenameWithoutExtension, String filenameWithExtension) throws IOException;
        
    /** 
     * A unique identifier used for this comparison-method in test assertions.
     */
    protected abstract String identifierForTest();
}
