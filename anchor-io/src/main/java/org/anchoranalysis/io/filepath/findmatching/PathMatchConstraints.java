/* (C)2020 */
package org.anchoranalysis.io.filepath.findmatching;

import com.google.common.base.Preconditions;
import java.nio.file.Path;
import java.util.function.Predicate;

/** Some constraints on which paths to match */
public class PathMatchConstraints {

    /** Only accepts files where the predicate returns TRUE */
    private Predicate<Path> matcherFile;

    /** Only accepts any containing directories where the predicate returns TRUE */
    private Predicate<Path> matcherDir;

    /** Limits on the depth of how many sub-directories are recursed */
    private int maxDirDepth;

    public PathMatchConstraints(
            Predicate<Path> matcherFile, Predicate<Path> matcherDir, int maxDirDepth) {
        Preconditions.checkArgument(maxDirDepth >= 0);
        this.matcherFile = matcherFile;
        this.matcherDir = matcherDir;
        this.maxDirDepth = maxDirDepth;
    }

    public PathMatchConstraints replaceMaxDirDepth(int replacementMaxDirDepth) {
        return new PathMatchConstraints(matcherFile, matcherDir, replacementMaxDirDepth);
    }

    public Predicate<Path> getMatcherFile() {
        return matcherFile;
    }

    public Predicate<Path> getMatcherDir() {
        return matcherDir;
    }

    public int getMaxDirDepth() {
        return maxDirDepth;
    }
}
