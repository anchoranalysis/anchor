/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.series;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;

public class ImgStackSeries implements Iterable<Stack> {

    private List<Stack> delegate = new ArrayList<>();

    /** Puts all stacks in the series into a single image stack */
    public Stack createSingleImgStack() throws IncorrectImageSizeException {

        Stack stackOut = new Stack();

        for (Stack stack : delegate) {
            for (Channel chnl : stack) {
                stackOut.addChnl(chnl);
            }
        }

        return stackOut;
    }

    @Override
    public Iterator<Stack> iterator() {
        return delegate.iterator();
    }

    public boolean add(Stack e) {
        return delegate.add(e);
    }

    public Stack get(int index) {
        return delegate.get(index);
    }

    public int size() {
        return delegate.size();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    public Collection<Stack> asCollection() {
        return delegate;
    }
}
