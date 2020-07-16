/*-
 * #%L
 * anchor-io
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
package org.anchoranalysis.io.params;

/**
 * An alternative mode of option where behaviours change.
 *
 * <p><div>e.g.
 *
 * <ul>
 *   <li>A single-file is executed only.
 *   <li>Output paths may change.
 *   <li>Logging output may change.
 * </ul>
 *
 * </div>
 *
 * @author Owen Feehan
 */
public class DebugModeParams {

    /** If null, this is disabled * */
    private String contains = null;

    /**
     * Constructor
     *
     * @param contains an optional string used to filter inputs. If NULL, then disabled.
     */
    public DebugModeParams(String contains) {
        super();
        this.contains = contains;
    }

    /** @return contains, or an empty string if null. */
    public String containsOrEmpty() {
        if (contains != null) {
            return contains;
        } else {
            return "";
        }
    }
}
