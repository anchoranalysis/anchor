package org.anchoranalysis.io.filepath.prefixer;

/*-
 * #%L
 * anchor-io
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

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.anchoranalysis.core.file.PathUtilities;
import org.anchoranalysis.io.filepath.prefixer.FilePathDifferenceFromFolderPath;
import org.junit.Test;

public class FilePathDifferenceFromFolderPathTest {

	private Path resolve( String path ) {
		// We treat it as a UNIX path so the tests will work on all platforms
		return Paths.get( PathUtilities.toStringUnixStyle(path) );
	}
	
	// START NORMAL-BEHAVIOUR
	
	@Test
	public void testFolderAbsoluteWindowsFolder() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.init(
			resolve("c:\\somebase\\"),
			resolve("c:\\somebase\\someDir1\\someDir2\\someFile1.txt")
		);			
		
		assertTrue( fdd.getFolder().equals( resolve("someDir1\\someDir2") ) );
		assertTrue( fdd.getFilename().equals("someFile1.txt") );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFolderAbsoluteWindowsNonFolderBase() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.init(
			resolve("c:\\somebase\\base_"),
			resolve("c:\\somebase\\base_someDir1\\someDir2\\someFile1.txt")
		);			
	}
	
	@Test
	public void testFolderAbsoluteUnixFolder() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.init(
			resolve("/somebase/"),
			resolve("/somebase/someDir1/someDir2/someFile1.txt")
		);			
		
		assertTrue( fdd.getFolder().equals( resolve("someDir1/someDir2") ) );
		assertTrue( fdd.getFilename().equals("someFile1.txt") );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFolderAbsoluteUnixNonFolderBase() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.init(
			resolve("/somebase/extra_"),
			resolve("/somebase/someDir1/someDir2/someFile1.txt")
		);			
		
	}

	@Test
	public void testFolderRelativeWindowsFolder() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.init(
			resolve("..\\somebase\\"),
			resolve("..\\somebase\\someDir1\\someDir2\\someFile1.txt")
		);			
		
		assertTrue( fdd.getFolder().equals( resolve("someDir1\\someDir2") ) );
		assertTrue( fdd.getFilename().equals("someFile1.txt") );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFolderRelativeWindowsNonFolderBase() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.init(
			resolve("..\\somebase\\base_"),
			resolve("..\\somebase\\base_someDir1\\someDir2\\someFile1.txt")
		);			
	}
	
	@Test
	public void testFolderRelativeUnixFolder() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.init(
			resolve("../somebase/"),
			resolve("../somebase/someDir1/someDir2/someFile1.txt")
		);			
		
		assertTrue( fdd.getFolder().equals( resolve("someDir1/someDir2") ) );
		assertTrue( fdd.getFilename().equals("someFile1.txt") );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFolderRelativeUnixNonFolderBase() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.init(
			resolve("../somebase/extra_"),
			resolve("../somebase/someDir1/someDir2/someFile1.txt")
		);			
		
	}
	
	// END NORMAL-BEHAVIOUR	
	
	
	
	// START DIRECT
	
	@Test
	public void testFolderAbsoluteWindowsFolderDirect() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.initDirect(
			resolve("c:\\somebase\\"),
			resolve("c:\\somebase\\someDir1\\someDir2\\someFile1.txt")
		);			
		
		assertTrue( fdd.getFolder().equals( resolve("someDir1\\someDir2") ) );
		assertTrue( fdd.getFilename().equals("someFile1.txt") );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFolderAbsoluteWindowsNonFolderBaseDirect() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.initDirect(
			resolve("c:\\somebase\\base_"),
			resolve("c:\\somebase\\base_someDir1\\someDir2\\someFile1.txt")
		);			
	}
	
	@Test
	public void testFolderAbsoluteUnixFolderDirect() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.initDirect(
			resolve("/somebase/"),
			resolve("/somebase/someDir1/someDir2/someFile1.txt")
		);			
		
		assertTrue( fdd.getFolder().equals( resolve("someDir1/someDir2") ) );
		assertTrue( fdd.getFilename().equals("someFile1.txt") );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFolderAbsoluteUnixNonFolderBaseDirect() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.initDirect(
			resolve("/somebase/extra_"),
			resolve("/somebase/someDir1/someDir2/someFile1.txt")
		);			
		
	}

	@Test
	public void testFolderRelativeWindowsFolderDirect() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.initDirect(
			resolve("..\\somebase\\"),
			resolve("..\\somebase\\someDir1\\someDir2\\someFile1.txt")
		);			
		
		assertTrue( fdd.getFolder().equals( resolve("someDir1\\someDir2") ) );
		assertTrue( fdd.getFilename().equals("someFile1.txt") );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFolderRelativeWindowsNonFolderBaseDirect() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.initDirect(
			resolve("..\\somebase\\base_"),
			resolve("..\\somebase\\base_someDir1\\someDir2\\someFile1.txt")
		);			
	}
	
	@Test
	public void testFolderRelativeUnixFolderDirect() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.initDirect(
			resolve("../somebase/"),
			resolve("../somebase/someDir1/someDir2/someFile1.txt")
		);			
		
		assertTrue( fdd.getFolder().equals( resolve("someDir1/someDir2") ) );
		assertTrue( fdd.getFilename().equals("someFile1.txt") );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFolderRelativeUnixNonFolderBaseDirect() throws IOException {
		
		FilePathDifferenceFromFolderPath fdd = new FilePathDifferenceFromFolderPath();
		fdd.initDirect(
			resolve("../somebase/extra_"),
			resolve("../somebase/someDir1/someDir2/someFile1.txt")
		);			
		
	}
	
	// END DIRECT
	
}
