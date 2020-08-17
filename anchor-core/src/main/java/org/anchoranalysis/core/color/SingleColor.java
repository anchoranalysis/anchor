package org.anchoranalysis.core.color;

import java.awt.Color;
import lombok.AllArgsConstructor;

/**
 * An index that always uses the same single color
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class SingleColor implements ColorIndex {

    private final RGBColor color;
    
    public SingleColor(Color colorAwt) {
        color = new RGBColor(colorAwt);
    }
    
    @Override
    public RGBColor get(int index) {
        return color;
    }

    @Override
    public int numUniqueColors() {
        return 1;
    }

    @Override
    public boolean has(int index) {
        return true;
    }
}
