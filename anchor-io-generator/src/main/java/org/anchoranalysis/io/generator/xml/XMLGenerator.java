package org.anchoranalysis.io.generator.xml;

import org.anchoranalysis.io.generator.SingleFileTypeGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

public abstract class XMLGenerator extends SingleFileTypeGenerator {

	@Override
	public String getFileExtension(OutputWriteSettings outputWriteSettings) {
		return outputWriteSettings.getExtensionXML();
	}

}
