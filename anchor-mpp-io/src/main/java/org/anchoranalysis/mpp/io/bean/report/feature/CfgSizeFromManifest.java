package org.anchoranalysis.mpp.io.bean.report.feature;

/*-
 * #%L
 * anchor-mpp-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.experiment.log.ConsoleLogReporter;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.ManifestRecorderFile;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.io.manifest.reportfeature.ReportFeatureForManifest;

public class CfgSizeFromManifest extends ReportFeatureForManifest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private String fileName = "";
	
	@BeanField
	private String title = "";
	// END BEAN PROPERTIES

	@Override
	public boolean isNumeric() {
		return true;
	}

	@Override
	public String genTitleStr() throws OperationFailedException {
		return title;
	}

	@Override
	public String genFeatureStrFor(ManifestRecorderFile obj, LogErrorReporter logger)
			throws OperationFailedException {

		FinderSerializedObject<Cfg> finder = new FinderSerializedObject<>("cfg", new ErrorReporterIntoLog(new ConsoleLogReporter()));
		
		ManifestRecorder manifest;
		try {
			manifest = obj.doOperation();
		} catch (ExecuteException e) {
			throw new OperationFailedException(e);
		}
		
		if (!finder.doFind( manifest )) {
			throw new OperationFailedException( String.format("Cannot find '%s' in manifest",fileName) );
		}
		
		try {
			return Integer.toString(finder.get().size());
		} catch (GetOperationFailedException e) {
			throw new OperationFailedException(e);
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
