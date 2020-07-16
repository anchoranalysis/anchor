/* (C)2020 */
package org.anchoranalysis.feature.nrg;

import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.Stack;

// An NRG stack with associated parameters
public class NRGStackWithParams {

    private NRGStack nrgStack;
    private KeyValueParams params;

    public NRGStackWithParams(Channel chnl) {
        super();
        this.nrgStack = new NRGStack(chnl);
        this.params = new KeyValueParams();
    }

    public NRGStackWithParams(NRGStack nrgStack) {
        super();
        this.nrgStack = nrgStack;
        this.params = new KeyValueParams();
    }

    public NRGStackWithParams(NRGStack nrgStack, KeyValueParams params) {
        super();
        this.nrgStack = nrgStack;
        this.params = params;
    }

    public NRGStackWithParams(Stack stackIn, KeyValueParams params) {
        this.nrgStack = new NRGStack(stackIn);
        this.params = params;
    }

    public NRGStackWithParams(Stack stackIn) {
        this.nrgStack = new NRGStack(stackIn);
        this.params = new KeyValueParams();
    }

    public NRGStackWithParams(ImageDimensions dimensions) {
        this.nrgStack = new NRGStack(dimensions);
        this.params = new KeyValueParams();
    }

    public NRGStackWithParams extractSlice(int z) {
        return new NRGStackWithParams(nrgStack.extractSlice(z), params);
    }

    public NRGStack getNrgStack() {
        return nrgStack;
    }

    public KeyValueParams getParams() {
        return params;
    }

    public ImageDimensions getDimensions() {
        return nrgStack.getDimensions();
    }

    public void setParams(KeyValueParams params) {
        this.params = params;
    }

    public NRGStackWithParams copyChangeParams(KeyValueParams paramsToAssign) {
        return new NRGStackWithParams(nrgStack, paramsToAssign);
    }

    public Channel getChnl(int index) {
        return nrgStack.getChnl(index);
    }
}
