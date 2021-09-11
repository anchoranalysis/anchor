/*-
 * #%L
 * anchor-test
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.test;

import java.io.File;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Prints a list of the files in a directory in a user-friendly way to {@code stdout}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PrintFilesInDirectory {

    /**
     * Prints a list of the files in a directory recursively.
     *
     * @param directoryPath path to the directory to print all files from.
     */
    public static void printRecursively(String directoryPath) {
        printRecursively(directoryPath, 0);
    }

    /**
     * Prints a list of the files in a directory recursively.
     *
     * @param directoryPath path to the directory to print all files from.
     * @param numberTabsPrefixing how many tabs to print before a file, to given a hierarchical
     *     whitespace structure
     */
    private static void printRecursively(String directoryPath, int numberTabsPrefixing) {
        File directory = new File(directoryPath);

        // All files on the current hierarchical level, where files also includes sub-directories.
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files) {
            printSeveralTabs(numberTabsPrefixing);
            if (file.isDirectory()) {
                printDirectoryName(file.getName());
                printRecursively(file.getAbsolutePath(), numberTabsPrefixing + 1);
            } else {
                printFileName(file.getName()); // NOSONAR
            }
        }
    }

    private static void printDirectoryName(String directoryName) {
        System.out.println("[" + directoryName + "]"); // NOSONAR
    }

    private static void printFileName(String fileName) {
        System.out.println(fileName); // NOSONAR
    }

    private static void printSeveralTabs(int numberTabs) {
        for (int i = 0; i < numberTabs; i++) {
            System.out.print("\t"); // NOSONAR
        }
    }
}
