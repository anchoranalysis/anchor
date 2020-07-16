/* (C)2020 */
package org.anchoranalysis.io.bean.file.matcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;
import org.apache.commons.lang.SystemUtils;

class HiddenPathChecker {

    private HiddenPathChecker() {}

    public static boolean includePath(Path path) {
        try {
            // There is a bug in Java (apparently fixed in version 13) where Files.isHidden
            //  does not recognize directories as hidden.
            return !path.toFile().exists() || !isHidden(path);
        } catch (Exception e) {
            // If we can't perform these operations, we consider the file not to be hidden
            // rather than throwing an exception
            return true;
        }
    }

    /*
     * A workaround for a bug in Java (apparently fixed in version 13) where {@link Files.isHidden}
     *  does not recognise directories as being hidden.
     *
     * <p>See <a href="https://stackoverflow.com/questions/53791740/why-does-files-ishiddenpath-return-false-for-directories-on-windows">Stack Overflow</a></p>
     **/
    private static boolean isHidden(Path path) throws IOException {
        try {
            if (SystemUtils.IS_OS_WINDOWS) {
                DosFileAttributes dosFileAttributes =
                        Files.readAttributes(
                                path, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                return dosFileAttributes.isHidden();
            } else {
                // Note a {@link ArrayIndexOutOfBoundsException} is being thrown here when running
                // on
                // the Linux subsystem of Windows. It's caught in {@link includePath}.
                return Files.isHidden(path);
            }
        } catch (UnsupportedOperationException e) {
            return Files.isHidden(path);
        }
    }
}
