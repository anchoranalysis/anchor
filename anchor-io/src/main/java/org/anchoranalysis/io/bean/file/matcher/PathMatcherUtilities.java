/* (C)2020 */
package org.anchoranalysis.io.bean.file.matcher;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PathMatcherUtilities {

    public static Predicate<Path> filter(Path dir, String fileType, String fileFilter) {
        PathMatcher matcher = PathMatcherUtilities.matcherForFilter(dir, fileType, fileFilter);
        return p -> PathMatcherUtilities.acceptPathViaMatcher(p, matcher);
    }

    private static boolean acceptPathViaMatcher(Path path, PathMatcher matcher) {
        return matcher.matches(path.getFileName());
    }

    private static PathMatcher matcherForFilter(Path dir, String filterType, String fileFilter) {
        return dir.getFileSystem().getPathMatcher(filterType + ":" + fileFilter);
    }
}
