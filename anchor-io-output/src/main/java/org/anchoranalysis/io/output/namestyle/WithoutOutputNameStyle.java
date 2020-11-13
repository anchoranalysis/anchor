package org.anchoranalysis.io.output.namestyle;

import java.util.Optional;

/**
 * Avoids including an output-name in the filename.
 * 
 * @author Owen Feehan
 *
 */
public class WithoutOutputNameStyle extends OutputNameStyle {

    private static final long serialVersionUID = 1L;

    public WithoutOutputNameStyle(String outputName) {
        super(outputName);
    }
    
    @Override
    public Optional<String> filenameWithoutExtension() {
        return Optional.empty();
    }

    @Override
    public OutputNameStyle duplicate() {
        return new WithoutOutputNameStyle(getOutputName());
    }

}
