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

package org.anchoranalysis.image.io.stack.input;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.bean.provider.stack.Arrange;
import org.anchoranalysis.image.bean.spatial.arrange.overlay.Overlay;
import org.anchoranalysis.image.bean.spatial.arrange.tile.Tile;
import org.anchoranalysis.image.io.bean.stack.combine.StackProviderWithLabel;
import org.anchoranalysis.image.io.bean.stack.combine.TextStyle;
import org.anchoranalysis.image.io.bean.stack.combine.WriteText;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TileRasters {

    public static Arrange createStackProvider(
            List<StackProviderWithLabel> list,
            int numberColumns,
            boolean createShort,
            boolean scaleLabel,
            boolean expandLabelZ) {

        // Makes everything an unsigned-short and using RGB output
        Arrange arrange = new Arrange(createShort, true);

        // Add stack providers
        for (StackProviderWithLabel provider : list) {
            arrange.addStack(provider.getStack());
            arrange.addStack(addGenerateString(provider, createShort, scaleLabel, expandLabelZ));
        }
        arrange.setArrange(createTile(numberColumns, list.size()));

        return arrange;
    }

    private static Tile createTile(int numberColumns, int numberProviders) {
        Tile tile = new Tile();
        tile.setNumberColumns(numberColumns);
        tile.setNumberRows(integerDivisionRoundUp(numberProviders, numberColumns));
        tile.setCellDefault(new Overlay("left", "top", "repeat"));
        return tile;
    }

    private static WriteText addGenerateString(
            StackProviderWithLabel providerWithLabel,
            boolean createShort,
            boolean scaleLabel,
            boolean expandLabelZ) {

        WriteText out = new WriteText(providerWithLabel.getLabel());
        out.setStyle(new TextStyle());
        out.setCreateShort(createShort);
        if (scaleLabel) {
            out.setIntensityProvider(providerWithLabel.getStack());
        }
        if (expandLabelZ) {
            out.setRepeatZProvider(providerWithLabel.getStack());
        }
        return out;
    }

    private static int integerDivisionRoundUp(int dividend, int divisor) {
        return (int) Math.ceil(((double) dividend) / divisor);
    }
}
