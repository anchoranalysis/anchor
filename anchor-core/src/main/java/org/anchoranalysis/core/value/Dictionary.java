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
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;

/**
 * Collection of parameters represented by key-value pairs.
 *
 * <p>Values are always strings.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Dictionary {

    /** Container for key-value pairs, not exposed externally. */
    private Properties properties;

    /** Creates empty, with no parameters. */
    public Dictionary() {
        properties = new Properties();
    }

    /**
     * Reads parameters from <a
     * href="https://docs.oracle.com/javase/tutorial/essential/environment/properties.html">a Java
     * properties file</a>.
     *
     * @param path the path where the properties file is located.
     * @return a newly created dictionary
     * @throws IOException if the path doesn't exist, is in the correct format, or otherwise cannot
     *     be read.
     */
    public static Dictionary readFromFile(Path path) throws IOException {
        try (FileInputStream stream = new FileInputStream(path.toFile())) {
            Properties properties = new Properties();
            properties.loadFromXML(stream);
            return new Dictionary(properties);
        }
    }

    /**
     * Deep-copy of existing dictionary.
     *
     * @return a newly created dictionary, containing identical key-values.
     */
    public Dictionary duplicate() {
        Dictionary out = new Dictionary();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            checkEntryTyping(entry);
            out.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return out;
    }

    /**
     * Retrieves a value from the dictionary as a {@link String}.
     *
     * @param key the key to retrieve
     * @return the value if {@code key} exists, otherwise {@link Optional#empty()}.
     */
    public Optional<String> getAsString(String key) {
        return Optional.ofNullable(properties.getProperty(key));
    }

    /**
     * Retrieves a value from the dictionary, and converts to a {@code double}.
     *
     * @param key the key to retrieve
     * @return the value if {@code key} exists, otherwise {@code Double.NaN}.
     */
    public double getAsDouble(String key) {
        Optional<String> optional = getAsString(key);
        if (!optional.isPresent()) {
            return Double.NaN;
        } else if (optional.get().equals("NA")) {
            return Double.NaN;
        } else {
            return Double.valueOf(optional.get());
        }
    }

    /**
     * Inserts a key-value pair.
     *
     * <p>If the key already exists, its value is replaced.
     *
     * @param key the key to insert
     * @param value the value corresponding to {@code key}
     */
    public void put(String key, String value) {
        properties.put(key, value);
    }

    /**
     * Inserts a key-value pair, after converting {@code value} to a {@link String}.
     *
     * @param key the key to insert
     * @param value a floating-point value corresponding to {@code key}
     */
    public void put(String key, double value) {
        properties.put(key, Double.toString(value));
    }

    /**
     * Inserts a key/value pair, checking that the key doesn't already exist.
     *
     * @param key the key to insert
     * @param value a value corresponding to {@code key}
     * @throws OperationFailedException if {@code key} already exists in the dictionary.
     */
    public void putCheck(String key, String value) throws OperationFailedException {
        checkKeyNotPresent(key);
        put(key, value);
    }

    /**
     * Inserts a key/value pair, checking that the key doesn't already exist.
     *
     * @param key the key to insert
     * @param value a floating-point value corresponding to {@code key}
     * @throws OperationFailedException if {@code key} already exists in the dictionary.
     */
    public void putCheck(String key, double value) throws OperationFailedException {
        checkKeyNotPresent(key);
        put(key, value);
    }

    /**
     * Inserts all key/value pairs from another dictionary, checking that no key already exists.
     *
     * @param dictionary the dictionary to insert all key/value pairs from
     * @throws OperationFailedException if {@code key} already exists in the dictionary.
     */
    public void putCheck(Dictionary dictionary) throws OperationFailedException {
        for (Entry<Object, Object> entry : dictionary.properties.entrySet()) {
            checkEntryTyping(entry);
            putCheck((String) entry.getKey(), (String) entry.getValue());
        }
    }

    /**
     * All keys existing in the dictionary.
     *
     * <p>The set is not backed by the dictionary. Changes to the set have no impact on the contents
     * of the dictionary.
     *
     * @return a newly created set of all keys in the dictionary.
     */
    public Set<String> keys() {
        return properties.stringPropertyNames();
    }

    /**
     * Does a parameter exist with a particular key?
     *
     * @param key the key
     * @return true iff the parameter exists.
     */
    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    /**
     * Serializes the key-value parameters to a file.
     *
     * @param path path to write to.
     * @throws IOException if something goes wrong.
     */
    public void writeToFile(Path path) throws IOException {

        File file = path.toFile();
        try (FileOutputStream fop = new FileOutputStream(file)) {
            properties.storeToXML(fop, "");
        }
    }

    /** Checks that both the key and value of an entry are strings. */
    private void checkEntryTyping(Entry<Object, Object> entry) {
        if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String)) {
            throw new AnchorFriendlyRuntimeException("Non-string keys or values were encountered.");
        }
    }

    /** Throws an exception if a key is already present. */
    private void checkKeyNotPresent(String key) throws OperationFailedException {
        if (containsKey(key)) {
            throw new OperationFailedException(
                    String.format(
                            "Key %s already exists, refusing to create a new key in its place. Giving up.",
                            key));
        }
    }
}
