/*-
 * #%L
 * anchor-experiment
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

package org.anchoranalysis.experiment.io;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.experiment.bean.task.Task;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * Indicates and provides a mechanism that the {@link Task} can be replaced.
 *
 * @param <T> input-object type
 * @param <S> shared-state for job
 * @author Owen Feehan
 */
public interface ReplaceTask<T extends InputFromManager, S> {

    /**
     * Replace the currently-assigned {@link Task} with another.
     *
     * @param taskToReplace the task to replace.
     * @throws OperationFailedException if the task cannot be successfully replaced.
     */
    public void replaceTask(Task<T, S> taskToReplace) throws OperationFailedException;
}
