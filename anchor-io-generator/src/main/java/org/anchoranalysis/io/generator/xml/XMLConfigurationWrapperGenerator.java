/* (C)2020 */
package org.anchoranalysis.io.generator.xml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import javax.xml.transform.TransformerException;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.xml.XmlOutputter;
import org.apache.commons.configuration.XMLConfiguration;

@RequiredArgsConstructor
public class XMLConfigurationWrapperGenerator extends XMLGenerator {

    private final XMLConfiguration config;

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {
        assert (config != null);
        try {
            XmlOutputter.writeXmlToFile(config.getDocument(), filePath);
        } catch (TransformerException | IOException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("xml", "xmlConfigurationWrapper"));
    }
}
