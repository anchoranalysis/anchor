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

package org.anchoranalysis.image.io.bean.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.core.channel.Channel; // NOSONAR

/**
 * A particular {@link Channel} to be read from a file, identified by its position (index) in the
 * file.
 *
 * <p>A name is also assigned by the user.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class IndexedChannel extends AnchorBean<IndexedChannel> {

    // START BEAN PROPERTIES
    /**
     * The name of the {@link Channel} this entry represents.
     *
     * <p>This has no link to the underlying file, and may be assigned by the user, as makes sense.
     */
    @BeanField @Getter @Setter private String name;

    /** The index (starting at zero) of this {@link Channel} relative to others. */
    @BeanField @Getter @Setter private int index;
    // END BEAN PROPERTIES
}
