package org.anchoranalysis.io.generator.csv;

import org.anchoranalysis.io.generator.SingleFileTypeGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

public abstract class CSVGenerator extends SingleFileTypeGenerator {

	private String manifestFunction;
	
	protected CSVGenerator( String manifestFunction ) {
		this.manifestFunction = manifestFunction;
	}
	
	@Override
	public String getFileExtension(OutputWriteSettings outputWriteSettings) {
		return "csv";
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("csv", manifestFunction);
	}
}
