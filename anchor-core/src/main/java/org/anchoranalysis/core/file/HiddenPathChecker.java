package org.anchoranalysis.core.file;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;
import java.util.function.Predicate;

import org.apache.commons.lang.SystemUtils;

class HiddenPathChecker {
	
	private boolean ignoreHidden;
	HiddenPathChecker( boolean ignoreHidden ) {
		this.ignoreHidden = ignoreHidden;
	}
	
	public Predicate<Path> addHiddenCheck( Predicate<Path> func ) {
		return p -> func.test(p) && includePath(p);
	}
	
	public boolean includePath( Path path ) {
		
		if (!ignoreHidden) {
			return true;
		}
		
		try {
			// There is a big in Java (apparently fixed in version 13) where Files.isHidden
			//  does not recognise directories as hidden. Ther
			return !Files.exists(path) || !isHidden(path);
		} catch (IOException e) {
			// If we can't perform these operations, we consider the file not to be hidden
			// rather than throwing an exception
			return true;
		}
	}
	
	// A workaround for a bug in Java (apparently fixed in version 13) where Files.isHidden
	//  does not recognise directories as being hidden.
	// https://stackoverflow.com/questions/53791740/why-does-files-ishiddenpath-return-false-for-directories-on-windows
	private boolean isHidden(Path path) throws IOException {
		
		try {
			if (SystemUtils.IS_OS_WINDOWS) {
				DosFileAttributes dosFileAttributes = Files.readAttributes(
					path,
					DosFileAttributes.class,
					LinkOption.NOFOLLOW_LINKS
				);
				return dosFileAttributes.isHidden();
			} else {
				return Files.isHidden(path);
			}
		} catch (UnsupportedOperationException e) {
			return Files.isHidden(path);
		}
	}
}
