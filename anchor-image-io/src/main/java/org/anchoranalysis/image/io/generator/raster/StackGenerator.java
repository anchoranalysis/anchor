/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster;

import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class StackGenerator extends RasterGenerator
        implements IterableObjectGenerator<Stack, Stack> {

    private Stack stackIn;
    private boolean padIfNec;
    private String manifestFunction;

    // Won't do any padding
    public StackGenerator(String manifestFunction) {
        this(false, manifestFunction);
    }

    public StackGenerator(boolean padIfNec, String manifestFunction) {
        super();
        this.padIfNec = padIfNec;
        this.manifestFunction = manifestFunction;
    }

    // Notes pads the passed channel, would be better if it makes a new stack first
    public StackGenerator(Stack stack, boolean padIfNec, String manifestFunction) {
        super();
        this.stackIn = stack;
        this.padIfNec = padIfNec;
        this.manifestFunction = manifestFunction;
    }

    public static Stack generateImgStack(Stack stackIn, boolean padIfNec)
            throws OutputWriteFailedException {
        Stack stackOut = new Stack();

        try {
            for (int c = 0; c < stackIn.getNumChnl(); c++) {
                stackOut.addChnl(stackIn.getChnl(c));
            }
        } catch (IncorrectImageSizeException e) {
            throw new OutputWriteFailedException(e);
        }

        try {
            if (padIfNec && stackOut.getNumChnl() == 2) {
                stackOut.addBlankChnl();
            }
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }

        return stackOut;
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {
        assert (stackIn != null);
        return generateImgStack(stackIn, padIfNec);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", manifestFunction));
    }

    @Override
    public ObjectGenerator<Stack> getGenerator() {
        return this;
    }

    @Override
    public Stack getIterableElement() {
        return stackIn;
    }

    @Override
    public void setIterableElement(Stack element) {
        this.stackIn = element;
    }

    @Override
    public void end() throws OutputWriteFailedException {
        this.stackIn = null;
    }

    @Override
    public boolean isRGB() {
        return stackIn.getNumChnl() == 3 || (stackIn.getNumChnl() == 2 && padIfNec);
    }
}
