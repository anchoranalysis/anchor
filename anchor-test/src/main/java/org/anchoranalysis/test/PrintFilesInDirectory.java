package org.anchoranalysis.test;

import java.io.File;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * Prints a list of the files in a directory in a user-friendly way to {@code stdout}.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
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
     * @param numberTabsPrefixing how many tabs to print before a file, to given a hierarchical whitespace structure
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
