/*-
 * #%L
 * anchor-image-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.io.stack;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.provider.Provider;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterOverlay;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterTile;
import org.anchoranalysis.image.bean.provider.stack.ArrangeRaster;
import org.anchoranalysis.image.io.bean.stack.StackProviderWithLabel;
import org.anchoranalysis.image.io.bean.stack.provider.GenerateString;
import org.anchoranalysis.image.io.generator.raster.StringRasterGenerator;
import org.anchoranalysis.image.stack.Stack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TileRasters {

    public static ArrangeRaster createStackProvider(
            List<StackProviderWithLabel> list,
            int numCols,
            boolean createShort,
            boolean scaleLabel,
            boolean expandLabelZ) {

        ArrangeRaster spar = new ArrangeRaster();
        spar.setCreateShort(createShort);
        spar.setForceRGB(true); // Makes everything an RGB output
        spar.setList(new ArrayList<Provider<Stack>>());

        // Add stack providers
        for (StackProviderWithLabel provider : list) {
            spar.getList().add(provider.getStack());
            spar.getList().add(addGenerateString(provider, createShort, scaleLabel, expandLabelZ));
        }

        ArrangeRasterTile art = new ArrangeRasterTile();
        art.setNumCols(numCols);
        art.setNumRows((int) Math.ceil(((double) list.size()) / numCols));

        ArrangeRasterOverlay arOverlay = new ArrangeRasterOverlay();
        arOverlay.setHorizontalAlign("left");
        arOverlay.setVerticalAlign("top");
        arOverlay.setZAlign("repeat");

        art.setCellDefault(arOverlay);

        spar.setArrange(art);

        return spar;
    }

    private static GenerateString addGenerateString(
            StackProviderWithLabel providerWithLabel,
            boolean createShort,
            boolean scaleLabel,
            boolean expandLabelZ) {
        
        GenerateString out = new GenerateString();
        out.setStringRasterGenerator( new StringRasterGenerator(providerWithLabel.getLabel(), 3) );
        out.setCreateShort(createShort);
        if (scaleLabel) {
            out.setIntensityProvider(providerWithLabel.getStack());
        }
        if (expandLabelZ) {
            out.setRepeatZProvider(providerWithLabel.getStack());
        }
        return out;
    }
}
