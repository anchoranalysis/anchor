package org.anchoranalysis.io.manifest.reportfeature;

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
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.io.manifest.ManifestRecorderFile;

public class RootFilePathRegEx extends ReportFeatureForManifest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private int groupNum;
	// END BEAN PROPERTIES
	
	@Override
	public String genFeatureStrFor(ManifestRecorderFile obj, LogErrorReporter logger) throws OperationFailedException {

		// We get the last three 
		Path path = obj.getRootPath();

		String sep = File.separatorChar == '\\' ? "\\\\" : "/";
		
		Pattern p = Pattern.compile(".*" + sep + "-?(.+)" + sep + "(.+)" + sep + "(.+)" + sep + "(.+)$");

		Matcher m = p.matcher(path.toString());
		
		if (!m.matches()) {
			throw new OperationFailedException("Does not match reg ex");
		}
		
		if (m.groupCount()!=4) {
			throw new OperationFailedException("Does not match reg ex (incorrect number of groups)");
		}
		
		if (groupNum==0) {
			return idFromRegEx(m);
		}
		
		return m.group(groupNum);
	}

	@Override
	public boolean isNumeric() {
		return groupNum>=2;
	}

	public int getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(int groupNum) {
		this.groupNum = groupNum;
	}

	@Override
	public String genTitleStr() throws OperationFailedException {
		switch (groupNum) {
		case 0:
			return "id";
		case 1:
			return "set";
		case 2:
			return "group";
		case 3:
			return "imageid";
		case 4:
			return "objid";
		default:
			throw new OperationFailedException("groupNum must be between 0 and 4 inclusive");
		}
	}

	private String idFromRegEx( Matcher m ) {
		return String.format(
			"%s_%s_%s_%s",
			m.group(1),
			m.group(2),
			m.group(3),
			m.group(4)
		);
	}
}
