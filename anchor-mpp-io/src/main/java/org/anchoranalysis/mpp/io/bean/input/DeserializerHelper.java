/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.bean.input;

import java.nio.file.Path;
import org.anchoranalysis.annotation.mark.DualMarksAnnotation;
import org.anchoranalysis.io.bean.deserializer.XStreamDeserializer;
import org.anchoranalysis.io.exception.DeserializationFailedException;
import org.anchoranalysis.mpp.mark.MarkCollection;

/**
 * @author Owen Feehan
 * @param <T> rejection-reason
 */
class DeserializerHelper<T> {

    private XStreamDeserializer<MarkCollection> deserializerMarks = new XStreamDeserializer<>();
    private XStreamDeserializer<DualMarksAnnotation<T>> deserializerAnnotation =
            new XStreamDeserializer<>();

    public MarkCollection deserializeMarks(Path path) throws DeserializationFailedException {
        return deserializerMarks.deserialize(path);
    }

    public MarkCollection deserializeMarksFromAnnotation(
            Path outPath, boolean includeAccepted, boolean includeRejected)
            throws DeserializationFailedException {
        DualMarksAnnotation<T> ann = deserializerAnnotation.deserialize(outPath);
        if (!ann.isFinished()) {
            throw new DeserializationFailedException("Annotation was never finished");
        }
        if (!ann.isAccepted()) {
            throw new DeserializationFailedException("Annotation was never accepted");
        }

        MarkCollection marksOut = new MarkCollection();

        if (includeAccepted) {
            marksOut.addAll(ann.marks());
        }

        if (includeRejected && ann.getMarksReject() != null) {
            marksOut.addAll(ann.getMarksReject());
        }

        return marksOut;
    }
}
