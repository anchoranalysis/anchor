/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.spatial.arrange;

import java.util.Iterator;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackArrangement;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Overlays one image on the other.
 *
 * <p>
 *
 * <ul>
 *   <li><b>first</b> image passed is assumed to be the source.
 *   <li><b>second</b> image passed is assumed to be the overlay.
 * </ul>
 *
 * <p>We have no Z implemented yet, so we always overlay at z position 0.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public class Overlay extends StackArranger {

    private static final PositionChoices CHOICES_X = new PositionChoices("left", "center", "right");
    private static final PositionChoices CHOICES_Y = new PositionChoices("top", "center", "bottom");
    private static final PositionChoices CHOICES_Z = new PositionChoices("bottom", "center", "top", Optional.of("repeat"));
    
	private static final Single SINGLE = new Single();
	
    // START BEAN PROPERTIES
	/** Indicates how to align the image across the <b>X-axis</b> (i.e. horizontally): one of {@code left, right, center}. */
    @BeanField @Getter @Setter private String alignX = "left";

	/** Indicates how to align the image across the <b>Y-axis</b> (i.e. vertically): one of {@code top, bottom, center}. */
    @BeanField @Getter @Setter private String alignY = "top";

    /** 
     * Indicates how to align the image across the <b>Z-axis</b>: one of {@code top, bottom, center, repeat}. 
     * 
     * <p>{@code repeat} is a special-case where a single z-slice overlay will be duplicated across the z-dimension of the stack onto which it is overlayed.
     */
    @BeanField @Getter @Setter private String alignZ = "top";
    // END BEAN PROPERTIES
    
    @Override
    public String describeBean() {
        return getBeanName();
    }
    
    @Override
    protected StackArrangement arrangeStacks(Iterator<RGBStack> stacks)
            throws ArrangeStackException {

        if (!stacks.hasNext()) {
            throw new ArrangeStackException("No image in iterator for source");
        }

        StackArrangement arrangement = SINGLE.arrangeStacks(stacks);

        if (!stacks.hasNext()) {
            throw new ArrangeStackException("No image in iterator for overlay");
        }

        RGBStack overlay = stacks.next();

        Extent overlaySize = deriveExtent(overlay.getChannel(0).extent(), arrangement.extent());

        Point3i cornerMin = new Point3i(
        		positionX(arrangement, overlay.dimensions()),
        		positionY(arrangement, overlay.dimensions()),
        		positionZ(arrangement, overlay.dimensions())
        );
        arrangement.add(new BoundingBox(cornerMin, overlaySize));
        return arrangement;
    }

    private int positionX(StackArrangement arrangement, Dimensions dimensions) throws ArrangeStackException {
    	return CHOICES_X.position("alignX", alignX, Extent::x, arrangement, dimensions);
    }

    private int positionY(StackArrangement arrangement, Dimensions dimensions) throws ArrangeStackException {
    	return CHOICES_Y.position("alignY", alignY, Extent::y, arrangement, dimensions);
    }

    private int positionZ(StackArrangement arrangement, Dimensions dimensions) throws ArrangeStackException {
    	return CHOICES_Z.position("alignZ", alignZ, Extent::z, arrangement, dimensions);
    }

    private Extent deriveExtent(Extent overlay, Extent box) {
        return new Extent(
                Math.min(overlay.x(), box.x()),
                Math.min(overlay.y(), box.y()),
                alignZ.equalsIgnoreCase("repeat") || (overlay.z() > box.z())
                        ? box.z()
                        : overlay.z());
    }
}
