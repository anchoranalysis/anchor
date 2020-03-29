package org.anchoranalysis.mpp.io.bean.report.feature;

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
