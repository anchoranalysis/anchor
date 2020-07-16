/* (C)2020 */
package org.anchoranalysis.image.io.stack;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterOverlay;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterTile;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProviderArrangeRaster;
import org.anchoranalysis.image.io.bean.stack.StackProviderGenerateString;
import org.anchoranalysis.image.io.bean.stack.arrange.StackProviderWithLabel;
import org.anchoranalysis.image.io.generator.raster.StringRasterGenerator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TileRasters {

    public static StackProviderArrangeRaster createStackProvider(
            List<StackProviderWithLabel> list,
            int numCols,
            boolean createShort,
            boolean scaleLabel,
            boolean expandLabelZ) {

        StackProviderArrangeRaster spar = new StackProviderArrangeRaster();
        spar.setCreateShort(createShort);
        spar.setForceRGB(true); // Makes everything an RGB output
        spar.setList(new ArrayList<StackProvider>());

        // Add stack providers
        for (StackProviderWithLabel spwl : list) {
            spar.getList().add(spwl.getStackProvider());
            spar.getList().add(addGenerateString(spwl, createShort, scaleLabel, expandLabelZ));
        }

        ArrangeRasterTile art = new ArrangeRasterTile();
        art.setNumCols(numCols);
        art.setNumRows((int) Math.ceil(((double) list.size()) / numCols));

        ArrangeRasterOverlay arOverlay = new ArrangeRasterOverlay();
        arOverlay.setHorizontalAlign("left");
        arOverlay.setVerticalAlign("top");
        arOverlay.setZAlign("repeat");

        art.setCellDefault(arOverlay);

        spar.setArrangeRaster(art);

        return spar;
    }

    private static StackProviderGenerateString addGenerateString(
            StackProviderWithLabel spwl,
            boolean createShort,
            boolean scaleLabel,
            boolean expandLabelZ) {
        StackProviderGenerateString spgs = new StackProviderGenerateString();

        StringRasterGenerator srg = new StringRasterGenerator(spwl.getLabel());
        srg.setText(spwl.getLabel());
        srg.setWidth(-1);
        srg.setHeight(-1);
        srg.setPadding(3);

        spgs.setStringRasterGenerator(srg);
        spgs.setCreateShort(createShort);
        if (scaleLabel) {
            spgs.setIntensityProvider(spwl.getStackProvider());
        }
        if (expandLabelZ) {
            spgs.setRepeatZProvider(spwl.getStackProvider());
        }
        return spgs;
    }
}
