package org.anchoranalysis.io.bean.provider.file;

/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.IOFileFilter;

class WalkToDepth extends DirectoryWalker<File> {

	private static class RejectAllFiles implements IOFileFilter {

		@Override
		public boolean accept(File file) {
			return true;
		}

		@Override
		public boolean accept(File dir, String name) {
			return true;
		}
		
	}
	
	private int exactDepth;
	private ProgressReporterMultiple prm;
	
	public WalkToDepth( int exactDepth, ProgressReporterMultiple prm ) {
		super( null, new RejectAllFiles(), exactDepth);
		this.exactDepth = exactDepth;
		this.prm = prm;
	}

	public List<File> findDirs(File root) throws IOException {
		List<File> results = new ArrayList<File>();
		walk(root, results);
		return results;
	}

	@Override
	protected void handleFile(File file, int depth, Collection<File> results) throws IOException {
		// NOTHING TO DO
	}

	protected boolean handleDirectory(File directory, int depth, Collection<File> results) {
		
		if (depth==1) {
			prm.incrWorker();
		}
		
		if (depth==exactDepth) {
			results.add( directory );
		}
		
		return depth <= exactDepth;
	}
}
