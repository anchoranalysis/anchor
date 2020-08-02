package org.anchoranalysis.core.index;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import lombok.Getter;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

/**
 * When a get operation fails for a particular key
 *
 * @author Owen Feehan
 */
public class GetOperationFailedException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 4847382915351464834L;

    @Getter private final String key;

    @Getter private final String message;

    public GetOperationFailedException(Exception e) {
        super(e);
        this.key = "";
        this.message = e.getMessage();
    }

    public GetOperationFailedException(int key, String message) {
        this(String.valueOf(key), message);
    }

    public GetOperationFailedException(int key, Throwable exc) {
        this(String.valueOf(key), exc);
    }

    public GetOperationFailedException(String key, String message) {
        super(String.format("An exception occurred getting '%s':%n%s", key, message));
        this.key = key;
        this.message = message;
    }

    public GetOperationFailedException(String key, Throwable exc) {
        this(key, exc.toString());
    }

    public OperationFailedException asOperationFailedException() {
        return new OperationFailedException(getMessage());
    }
}
