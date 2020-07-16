/* (C)2020 */
package org.anchoranalysis.image.io.bean.stack.arrange;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProviderArrangeRaster;
import org.anchoranalysis.image.io.stack.TileRasters;
import org.anchoranalysis.image.stack.Stack;

// A short-cut provider for tiling a number of stack providers with labels
public class StackProviderTileWithLabels extends StackProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<StackProviderWithLabel> list = new ArrayList<>();

    @BeanField @Getter @Setter private int numCols = 3;

    @BeanField @Getter @Setter boolean createShort;

    @BeanField @Getter @Setter boolean scaleLabel = true;

    @BeanField @Getter @Setter
    boolean expandLabelZ = false; // Repeats the label in the z-dimension to match the stackProvider
    // END BEAN PROPERTIES

    @Override
    public Stack create() throws CreateException {
        StackProviderArrangeRaster arrangeRaster =
                TileRasters.createStackProvider(
                        list, numCols, createShort, scaleLabel, expandLabelZ);
        try {
            arrangeRaster.initRecursive(getInitializationParameters(), getLogger());
        } catch (InitException e) {
            throw new CreateException(e);
        }
        return arrangeRaster.create();
    }
}
