package org.anchoranalysis.test.image;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit Rule that uses both a temporary-folder and a {@link DualComparer}.
 * 
 * @author Owen Feehan
 *
 */
public class DualComparerTemporaryFolder implements TestRule {

    private TemporaryFolder folder = new TemporaryFolder();
    
    private DualComparer comparer;
    
    private final String relativeResourcesRoot;
    
    public DualComparerTemporaryFolder(String relativeResourcesRoot) {
        this.relativeResourcesRoot = relativeResourcesRoot;
    }
    
    @Override
    public Statement apply(Statement base, Description description) {
        return folder.apply(base, description);
    }
    
    public Path resolveTemporaryFile(String filename) {
        return folder.getRoot().toPath().resolve(filename);
    }

    /**
     * See {@link DualComparer#compareTwoBinaryFiles(String)}.
     * 
     * @param path relative-path (compared to root of both loaders) of files to compare
     * @return true if both paths have binary-files that are bytewise identical
     * @throws IOException if something goes wrong with I/O
     */
    public boolean compareTwoBinaryFiles(String path) throws IOException {
        createComparerIfNeeded();
        return comparer.compareTwoBinaryFiles(path);
    }
    
    /** Lazy creation of the comparer, as the temporary-folder isn't available when {@link #apply} is called. */
    private void createComparerIfNeeded() {
        if (comparer==null) {
            comparer = DualComparerFactory.compareTemporaryFolderToTest(folder, Optional.empty(), relativeResourcesRoot);    
        }
    }
}
