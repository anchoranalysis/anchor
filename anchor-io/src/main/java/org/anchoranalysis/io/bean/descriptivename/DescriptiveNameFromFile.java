package org.anchoranalysis.io.bean.descriptivename;

/*
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.input.descriptivename.DescriptiveFile;

public abstract class DescriptiveNameFromFile extends AnchorBean<DescriptiveNameFromFile> {

	private static final String DEFAULT_ELSE_NAME = "unknownName";
	
	/** Like descriptiveNamesForCheckUniqueness but with a default for emptyName */
	public List<DescriptiveFile> descriptiveNamesForCheckUniqueness( Collection<File> files, Logger logger ) throws AnchorIOException {
		return descriptiveNamesForCheckUniqueness(files, DEFAULT_ELSE_NAME, logger);
	}
	
	/** Like descriptiveNames for but checks that the final list of descriptive-files all have unique descriptive-names */
	public List<DescriptiveFile> descriptiveNamesForCheckUniqueness( Collection<File> files, String elseName, Logger logger) throws AnchorIOException {
		List<DescriptiveFile> list = descriptiveNamesFor(files, elseName, logger);
		checkUniqueness(list);
		checkNoPredicate(list, DescriptiveNameFromFile::containsBackslash, "contain backslashes");
		checkNoPredicate(list, DescriptiveNameFromFile::emptyString, "contain an empty string");
		return list;
	}
	
	/**
	 * A descriptive-name for a file
	 * 
	 * @param file the file to extract a descriptive-name for
	 * @param elseName a fallback name to use if something goes wrong
	 * @return
	 * @throws AnchorIOException
	 */
	public DescriptiveFile descriptiveNameFor( File file, String elseName, Logger logger) {
		return descriptiveNamesFor( Arrays.asList(file), elseName, logger ).get(0);
	}
	
	/**
	 * Extracts a list of descriptive-names (with associated) file for some files
	 * 
	 * @param files the files
	 * @param elseName a string to use if an error occurs extracting the descriptive-name (used as a prefix with an index)
	 * @param logger the logger
	 * @return a list of identical size and order to files, corresponding to the extracted names
	 */
	public abstract List<DescriptiveFile> descriptiveNamesFor( Collection<File> files, String elseName, Logger logger );
	
	private static void checkUniqueness( List<DescriptiveFile> list ) throws AnchorIOException {
		Map<String,Long> countDescriptiveNames = list.stream().collect(
			Collectors.groupingBy(
				DescriptiveFile::getDescriptiveName,
				Collectors.counting()
			)
		);
		
		for (Map.Entry<String, Long> entry : countDescriptiveNames.entrySet()) {
			if(entry.getValue()>1) {
				throw new AnchorIOException(
					String.format(
						"The extracted descriptive-names are not unique for %s.%nThe following have the same descriptive-name:%n%s",
						entry.getKey(),
						keysWithDescriptiveName(entry.getKey(), list)
					)
				);
			}
		}
	}
	
	private static void checkNoPredicate( List<DescriptiveFile> list, Predicate<String> predFunc, String dscr ) throws AnchorIOException {
		long numWithBackslashes = list.stream()
				.filter( df-> predFunc.test(df.getDescriptiveName()) )
				.count();
		
		if(numWithBackslashes>0) {
			throw new AnchorIOException(
				String.format(
					"The following descriptive-names may not %s:%n%s",
					dscr,
					keysWithDescriptiveNamePredicate(predFunc, list)
				)
			);
		}
	}

	// For debugging if there is a non-uniqueness clash between two DescriptiveFiles
	private static String keysWithDescriptiveName( String descriptiveName, List<DescriptiveFile> list ) {
		return keysWithDescriptiveNamePredicate( dn->dn.equals(descriptiveName), list );
	}
	
	private static String keysWithDescriptiveNamePredicate( Predicate<String> pred, List<DescriptiveFile> list ) {
		List<String> matches = list.stream()
			.filter( df -> pred.test(df.getDescriptiveName()) )
			.map( df-> df.getPath().toString() )
			.collect( Collectors.toList() );
		
		return String.join(System.lineSeparator(), matches);
	}
	
	private static boolean containsBackslash( String str ) {
		return str.contains("\\");
	}
	
	private static boolean emptyString( String str ) {
		return str==null || str.isEmpty();
	}
	
}
