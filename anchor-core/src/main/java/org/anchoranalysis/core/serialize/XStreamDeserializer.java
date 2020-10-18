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

package org.anchoranalysis.core.serialize;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import java.nio.file.Path;

/**
 * Deserializes an object using the <a href="https://x-stream.github.io/">XStream library</a>.
 *
 * @see XStreamSerializer for the counterpart.
 * @author Owen Feehan
 * @param <T> type of object to deserialize
 */
public class XStreamDeserializer<T> implements Deserializer<T> {

    private static final String[] ALLOWED_NAMESPACES =
            new String[] {"org.anchoranalysis.**", "cern.colt.matrix.**"};

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(Path filePath) throws DeserializationFailedException {

        if (!filePath.toFile().exists()) {
            throw new DeserializationFailedException(
                    String.format("File '%s' does not exist", filePath));
        }

        XStream xstream = setupXStream();

        Object o = xstream.fromXML(filePath.toFile());

        return (T) o;
    }

    private XStream setupXStream() {
        XStream xstream = new XStream(new Xpp3Driver());
        XStream.setupDefaultSecurity(xstream); // to be removed after 1.5
        xstream.allowTypesByWildcard(ALLOWED_NAMESPACES);
        return xstream;
    }
}
