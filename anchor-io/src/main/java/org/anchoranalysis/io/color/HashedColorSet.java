/* (C)2020 */
package org.anchoranalysis.io.color;

import java.util.HashMap;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.color.generator.ColorSetGenerator;

// Associates an id with a color from an existing color set, and remembers the association
//  for next time
public class HashedColorSet implements ColorIndex {

    private HashMap<Integer, RGBColor> map = new HashMap<>();

    private ColorList colorList;

    private int crntIndex = 0;

    // Constructor
    public HashedColorSet(ColorSetGenerator colorSetGnrtr, int uniqueCols)
            throws OperationFailedException {
        super();
        this.colorList = colorSetGnrtr.generateColors(uniqueCols);
    }

    @Override
    public RGBColor get(int i) {
        Integer z = Integer.valueOf(i);
        RGBColor col = map.get(z);

        if (col == null) {
            col = colorList.get(crntIndex);

            // We reset if we have reached the end
            if (++crntIndex == colorList.size()) {
                crntIndex = 0;
            }

            // let's get the color
            map.put(z, col);
        }

        return col;
    }

    @Override
    public int numUniqueColors() {
        return colorList.numUniqueColors();
    }

    @Override
    public boolean has(int i) {
        return map.containsKey(i);
    }
}
