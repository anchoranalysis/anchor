/* (C)2020 */
package org.anchoranalysis.image.io.input;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequenceStore;
import org.anchoranalysis.io.output.bound.BoundIOContext;

public class StackInputInitParamsCreator {

    private StackInputInitParamsCreator() {}

    public static ImageInitParams createInitParams(
            ProvidesStackInput inputObject, BoundIOContext context)
            throws OperationFailedException {
        ImageInitParams soImage = ImageInitParamsFactory.create(context);
        addInput(soImage, inputObject);
        return soImage;
    }

    private static void addInput(ImageInitParams soImage, ProvidesStackInput inputObject)
            throws OperationFailedException {
        inputObject.addToStore(
                new WrapStackAsTimeSequenceStore(soImage.getStackCollection()),
                0,
                ProgressReporterNull.get());
    }
}
