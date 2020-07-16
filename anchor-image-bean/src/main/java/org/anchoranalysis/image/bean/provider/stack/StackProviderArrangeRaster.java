/* (C)2020 */
package org.anchoranalysis.image.bean.provider.stack;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterBean;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.RasterArranger;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactoryShort;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;

// Creates new stacks that tile each provider
public class StackProviderArrangeRaster extends StackProvider {

    // START BEAN
    @BeanField @Getter @Setter private ArrangeRasterBean arrangeRaster;

    @BeanField @Getter @Setter
    private boolean forceRGB = false; // Makes sure every stack is converted into 3 channels

    @BeanField @Getter @Setter private List<StackProvider> list = new ArrayList<>();

    @BeanField @Getter @Setter private boolean createShort = false;
    // END BEAN

    private void copyFirstChnlUntil3(Stack stack) {
        while (stack.getNumChnl() < 3) {
            try {
                stack.addChnl(stack.getChnl(0).duplicate());
            } catch (IncorrectImageSizeException e) {
                assert false;
            }
        }
    }

    @Override
    public Stack create() throws CreateException {

        if (list.isEmpty()) {
            throw new CreateException("At least one stack must be present in list");
        }

        List<RGBStack> rasterList = new ArrayList<>();
        for (StackProvider provider : list) {

            Stack stack = provider.create();

            if (forceRGB) {
                copyFirstChnlUntil3(stack);
            }
            rasterList.add(new RGBStack(stack));
        }

        RasterArranger rasterArranger = new RasterArranger();
        try {
            rasterArranger.init(arrangeRaster, rasterList);
        } catch (InitException e) {
            throw new CreateException(e);
        }

        ChannelFactorySingleType factory =
                createShort ? new ChannelFactoryShort() : new ChannelFactoryByte();

        return rasterArranger.createStack(rasterList, factory).asStack();
    }
}
