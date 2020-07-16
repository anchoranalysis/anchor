/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.define;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.io.input.InputForMPPBean;
import org.anchoranalysis.mpp.io.input.MPPInitParamsFactory;

public abstract class DefineOutputter extends AnchorBean<DefineOutputter> {

    // START BEAN PROPERTIES
    @BeanField @OptionalBean @Getter @Setter private Define define;

    @BeanField @Getter @Setter private boolean suppressSubfolders = false;

    @BeanField @Getter @Setter private boolean suppressOutputExceptions = false;
    // END BEAN PROPERTIES

    protected MPPInitParams createInitParams(InputForMPPBean input, BoundIOContext context)
            throws CreateException {
        return MPPInitParamsFactory.create(
                context, Optional.ofNullable(define), Optional.of(input));
    }

    protected MPPInitParams createInitParams(BoundIOContext context) throws CreateException {
        return MPPInitParamsFactory.create(context, Optional.ofNullable(define), Optional.empty());
    }

    protected MPPInitParams createInitParams(
            BoundIOContext context,
            Optional<NamedProvider<Stack>> stacks,
            Optional<NamedProvider<ObjectCollection>> objects,
            Optional<KeyValueParams> keyValueParams)
            throws CreateException {
        return MPPInitParamsFactory.createFromExistingCollections(
                context, Optional.ofNullable(define), stacks, objects, keyValueParams);
    }

    // General objects can be outputted
    protected void outputSharedObjects(ImageInitParams initParams, BoundIOContext context)
            throws OutputWriteFailedException {
        if (suppressOutputExceptions) {
            SharedObjectsOutputter.output(initParams, suppressSubfolders, context);
        } else {
            SharedObjectsOutputter.outputWithException(initParams, suppressSubfolders, context);
        }
    }

    protected void outputSharedObjects(MPPInitParams initParams, BoundIOContext context)
            throws OutputWriteFailedException {

        outputSharedObjects(initParams.getImage(), context);

        if (suppressOutputExceptions) {
            SharedObjectsOutputter.output(initParams, suppressSubfolders, context);
        } else {
            SharedObjectsOutputter.outputWithException(
                    initParams, context.getOutputManager(), suppressSubfolders);
        }
    }
}
