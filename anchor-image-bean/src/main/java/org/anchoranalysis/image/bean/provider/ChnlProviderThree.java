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
/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.channel.Channel;

public abstract class ChnlProviderThree extends ChnlProvider {

    // START BEAN PROPERTIES
    @BeanField private ChnlProvider chnl1;

    @BeanField private ChnlProvider chnl2;

    @BeanField private ChnlProvider chnl3;
    // END BEAN PROPERTIES

    @Override
    public Channel create() throws CreateException {

        return process(chnl1.create(), chnl2.create(), chnl3.create());
    }

    protected abstract Channel process(Channel chnl1, Channel chnl2, Channel chnl3)
            throws CreateException;

    public ChnlProvider getChnl1() {
        return chnl1;
    }

    public void setChnl1(ChnlProvider chnl1) {
        this.chnl1 = chnl1;
    }

    public ChnlProvider getChnl2() {
        return chnl2;
    }

    public void setChnl2(ChnlProvider chnl2) {
        this.chnl2 = chnl2;
    }

    public ChnlProvider getChnl3() {
        return chnl3;
    }

    public void setChnl3(ChnlProvider chnl3) {
        this.chnl3 = chnl3;
    }
}
