/* (C)2020 */
package org.anchoranalysis.anchor.plot.io;

import java.awt.image.BufferedImage;
import org.anchoranalysis.anchor.plot.GraphInstance;

public class GraphOutputter {

    private GraphOutputter() {}

    public static BufferedImage createBufferedImage(GraphInstance gi, int width, int height) {
        return gi.getChart().createBufferedImage(width, height);
    }
}
