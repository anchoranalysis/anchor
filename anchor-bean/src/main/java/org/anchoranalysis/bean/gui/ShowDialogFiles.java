package org.anchoranalysis.bean.gui;

/*
 * #%L
 * anchor-bean
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
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.anchoranalysis.core.file.FileMatcher;

/**
 * Presents a dialog to the user with a list of files, so that user
 *   selects one or more
 *   
 *   
 *   This class is included in the anchor-bean package (being a GUI component) as
 *     the IncludeBeanFactory will sometimes use it to prompt the user
 *   
 * @author Owen Feehan
 *
 */
public class ShowDialogFiles {
	
	private ShowDialogFiles() {
		// Only static access
	}
	
	
	/**
	 * Shows a dialog with a list of items and user selects one or more
	 * 
	 * @param items 			items to display via .toString()
	 * @param selectOneOnly 	the user can only select one item (as opposed to severl)
	 * @param defaultChoice 	default selection in list
	 * @param <T> 				the type of each item in the list
	 * @return the selected-file or NULL if nothing is selected
	 */
	public static <T> List<T> showDialogAndReturnList( Collection<T> items, boolean selectOneOnly, String defaultChoice) {
		
		List<T> listInputObjs = new ArrayList<>();
		listInputObjs.addAll( items );
		
		sortList( listInputObjs );
		
		String[] arr = new String[ listInputObjs.size() ];
		int i=0;
		for( T item : listInputObjs ) {
			arr[i++] = item.toString();
		}
		
		String title = "Select file" + (selectOneOnly ? "" : "(s)");
		ListDialog dialog = new ListDialog(null, true, arr, title, selectOneOnly, defaultChoice );
		
		List<T> listOut = new ArrayList<>();
		
		if (dialog.isSuccess()) {
			for( int index : dialog.getSelectedIndices()) {
				listOut.add( listInputObjs.get(index) );
			}
		}
		
		dialog.dispose();
		
		return listOut;
	}
	
	
	/**
	 * Shows a dialog with a list of files, and user selects one or more
	 * 
	 * @param pathDirectory a path to a directory containing files
	 * @param fileFilter a filter defining which files (of the form that is inputted to FileMatcher.fileMatching)
	 * @return the selected-file or NULL if nothing is selected
	 * @throws IOException if a problems occurs listing files
	 */
	public static List<String> showDialogFiles( Path pathDirectory, String fileFilter ) throws IOException {
		FileMatcher fileMatcher = new FileMatcher();
		Collection<File> files = fileMatcher.matchingFiles( pathDirectory, false, fileFilter, false, null );
		
		List<String> names = new ArrayList<>();
		for( File f : files) {
			names.add( f.getName() );
		}
		
		return showDialogAndReturnList( names, false, null );
	}

	
	/**
	 * Shows a dialog with a list of files, and user selects only one
	 * 
	 * @param pathDirectory a path to a directory containing files
	 * @param fileFilter a filter defining which files (of the form that is inputted to FileMatcher.fileMatching)
	 * @param defaultChoice the default selected item
	 * @return the selected-file or NULL if nothing is selected
	 * @throws IOException if a problems occurs listing files
	 */
	public static Path showDialogFilesSelectOne( Path pathDirectory, String fileFilter, String defaultChoice ) throws IOException {
		
		FileMatcher fileMatcher = new FileMatcher();
		Collection<File> files = fileMatcher.matchingFiles( pathDirectory, false, fileFilter, false, null );
		
		List<String> names = new ArrayList<>();
		for( File f : files) {
			names.add( f.getName() );
		}
		
		List<String> ret = showDialogAndReturnList( names, true, defaultChoice );
		if (ret.size()==1) {
			return pathDirectory.resolve( ret.get(0) );
		} else {
			return null;				
		}
	}
	
	private static <T> void sortList( List<T> list ) {
		java.util.Collections.sort(
			list,
			(a,b) -> a.toString().compareTo(b.toString())
		);
	}
}
