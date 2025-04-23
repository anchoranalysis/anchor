/*-
 * #%L
 * anchor-test-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.anchoranalysis.test.image;

import com.google.common.base.Preconditions;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Compares if two directories contain identical files (both the file-names and their contents)
 *
 * <p>Based on code from a post by <i>Ray Toal</i> on <a
 * href="https://stackoverflow.com/a/7199929">Stack Overflow</a>
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DirectoriesComparer {

    /**
     * Recursive comparison of two directories to determine if they identical files
     *
     * @param directory1 first directory
     * @param directory2 second directory
     * @return true if they have the same files exactly (both the same file-names and contents)
     */
    public static boolean areDirectoriesEqual(Path directory1, Path directory2) {
        return areDirectoriesEqual(directory1.toFile(), directory2.toFile());
    }

    /**
     * A list of files/sub-directories contained in a directory (non-recursively), sorted
     * lexicographically
     */
    private static File[] sortedFilesForDirectory(File directories) {
        File[] files = directories.listFiles();
        // Sort file-names lexicographically (default comparator)
        Arrays.sort(files);
        return files;
    }

    private static boolean areDirectoriesEqual(File dir1, File dir2) {
        Preconditions.checkArgument(dir1.isDirectory());
        Preconditions.checkArgument(dir2.isDirectory());

        File[] list1 = sortedFilesForDirectory(dir1);
        File[] list2 = sortedFilesForDirectory(dir2);
        if (list1.length != list2.length) {
            return false;
        }

        for (int i = 0; i < list2.length; i++) {
            if (list1[i].isFile() && list2[i].isFile()) {
                if (!compareFiles(list1[i], list2[i])) { // NOSONAR
                    return false;
                }
            } else if (list1[i].isDirectory() && list2[i].isDirectory()) {
                if (!areDirectoriesEqual(list1[i], list2[i])) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares two files to determine if they are identical.
     *
     * @param file1 first file.
     * @param file2 second file.
     * @return true if the files have the same content (byte by byte), false otherwise.
     */
    private static boolean compareFiles(File file1, File file2) {
        long index1 = file1.length();
        long index2 = file2.length();
        if (index1 != index2) {
            return false;
        }
        try {
            try (DataInputStream stream1 = streamFor(file1)) {
                try (DataInputStream stream2 = streamFor(file2)) {
                    while (true) {
                        byte byte1 = stream1.readByte();
                        byte byte2 = stream2.readByte();
                        if (byte1 != byte2) {
                            return false;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            return true;
        }
    }

    /** Creates a {@link DataInputStream} for a file */
    private static DataInputStream streamFor(File file) throws FileNotFoundException {
        return new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    }
}
