/*-
 * #%L
 * anchor-core
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

package org.anchoranalysis.core.value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.anchoranalysis.core.exception.OperationFailedException;

/** Parameters represented by key/value strings */
public class KeyValueParams {

    private Properties delegate;

    public KeyValueParams() {
        delegate = new Properties();
    }

    private KeyValueParams(Properties properties) {
        delegate = properties;
    }

    public KeyValueParams duplicate() {
        KeyValueParams out = new KeyValueParams();
        for (Entry<Object, Object> entry : delegate.entrySet()) {
            assert entry.getKey() instanceof String;
            assert entry.getValue() instanceof String;
            out.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return out;
    }

    public boolean containsKey(String key) {
        return delegate.containsKey(key);
    }

    public void putIfEmpty(String key, double value) throws OperationFailedException {

        checkAlreadyKeyPresent(key);

        put(key, value);
    }

    public void put(String key, double value) {
        delegate.put(key, Double.toString(value));
    }

    public void put(String key, String value) {
        delegate.put(key, value);
    }

    public void putAll(KeyValueParams src) throws OperationFailedException {
        for (Entry<Object, Object> entry : src.delegate.entrySet()) {
            assert entry.getKey() instanceof String;
            assert entry.getValue() instanceof String;
            putIfEmpty((String) entry.getKey(), (String) entry.getValue());
        }
    }

    public double getPropertyAsDouble(String key) {
        String str = getProperty(key);
        if (str == null) {
            return Double.NaN;
        } else if (str.equals("NA")) {
            return Double.NaN;
        } else {
            return Double.valueOf(str);
        }
    }

    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    public String getProperty(String key) {
        return delegate.getProperty(key);
    }

    /**
     * Serializes the key-value params to a file
     *
     * @param path path to write to
     * @throws IOException if something goes wrong
     */
    public void writeToFile(Path path) throws IOException {

        File file = path.toFile();
        try (FileOutputStream fop = new FileOutputStream(file)) {
            delegate.storeToXML(fop, "");
        }
    }

    public static KeyValueParams readFromFile(Path path) throws IOException {

        File file = path.toFile();
        try (FileInputStream fin = new FileInputStream(file)) {
            Properties props = new Properties();
            props.loadFromXML(fin);
            return new KeyValueParams(props);
        }
    }

    public Set<String> keySet() {
        return delegate.stringPropertyNames();
    }

    private void putIfEmpty(String key, String value) throws OperationFailedException {

        checkAlreadyKeyPresent(key);

        put(key, value);
    }

    private void checkAlreadyKeyPresent(String key) throws OperationFailedException {
        if (containsKey(key)) {
            throw new OperationFailedException(
                    String.format(
                            "Key %s already exists, refusing to create a new key in its place. Giving up.",
                            key));
        }
    }
}
