/* (C)2020 */
package org.anchoranalysis.io.generator.csv;

import java.util.Optional;
import org.anchoranalysis.io.generator.SingleFileTypeGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

public abstract class CSVGenerator extends SingleFileTypeGenerator {

    private String manifestFunction;

    protected CSVGenerator(String manifestFunction) {
        this.manifestFunction = manifestFunction;
    }

    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings) {
        return "csv";
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("csv", manifestFunction));
    }
}
