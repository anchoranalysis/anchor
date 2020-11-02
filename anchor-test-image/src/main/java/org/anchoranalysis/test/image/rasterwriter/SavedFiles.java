package org.anchoranalysis.test.image.rasterwriter;

import java.util.Optional;
import org.anchoranalysis.test.TestLoader;
import org.anchoranalysis.test.image.DualComparer;
import org.anchoranalysis.test.image.DualComparerFactory;
import org.junit.rules.TemporaryFolder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Access previously saved-files in resources.
 * 
 * <p>Different sets of saved-files exist, indexed by their file {@code extension}.
 * 
 * @author Owen Feehan
  */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class SavedFiles {

    private static final String SAVED_FILES_IN_RESOURCES_DIRECTORY = "stackWriter/formats/";

    /**
     * Creates a comparer between a directory and saved-files in a directory in the resources.
     * 
     * @param directory the directory (forms the first element to compare).
     * @param extension a file-extension to identify a set of saved-files (forms the second element to compare).
     * @return a newly created comparer.
     */
    public static DualComparer createComparer(TemporaryFolder directory, String extension) {
        return DualComparerFactory.compareTemporaryDirectoryToTest(
                directory, Optional.empty(), relativeResourcesDirectory(extension));
    }
    
    /**
     * Creates a loader for reading from a particular set of saved-files in a directory in the resources.
     * 
     * @param extension a file-extension to identify a set of saved-files (forms the second element to compare).
     * @return a newly created loader
     */
    public static TestLoader createLoader(String extension) {
        return TestLoader.createFromMavenWorkingDirectory(relativeResourcesDirectory(extension));
    }
    
    private static String relativeResourcesDirectory(String extension) {
        return SAVED_FILES_IN_RESOURCES_DIRECTORY + extension;
    }
}
