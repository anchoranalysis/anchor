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

package org.anchoranalysis.io.path;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.path.RootPath;
import org.anchoranalysis.io.exception.AnchorIOException;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

/**
 * A collection of root-paths indexed by their name.
 *
 * <p>Currently exists as a singleton.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RootPathMap {

    private static RootPathMap instance = null;

    private MultiMap map = new MultiValueMap();

    /**
     * Given a path, it splits the *root* portion of the path from the remainder, trying a root
     * identified by the rootName and debugMode (and if this fails, also trying with
     * debugMode==false)
     *
     * @param pathIn path to split
     * @param rootName identifier for a root to use
     * @param debugMode whether we are in debug mode or not
     * @return the split-path
     * @throws AnchorIOException if the path cannot be matched against the root
     */
    public SplitPath split(Path pathIn, String rootName, boolean debugMode)
            throws AnchorIOException {

        SplitPath out;

        try {
            RootPath firstRoot = findRoot(rootName, debugMode);
            out = firstRoot.split(pathIn);

        } catch (IllegalArgumentException e) {

            RootPath secondRoot = findRoot(rootName, false);
            if (debugMode) {
                out = secondRoot.split(pathIn);
            } else {
                throw e;
            }
        }

        return out;
    }

    public static RootPathMap instance() {
        if (instance == null) {
            instance = new RootPathMap();
        }
        return instance;
    }

    /**
     * Adds root-paths contained in a list of XML Beans in an xml file
     *
     * @param path the path to the XML file containing the root-paths
     * @throws OperationFailedException
     */
    public void addFromXmlFile(Path path) throws OperationFailedException {

        try {
            List<RootPath> listRootPaths = BeanXmlLoader.loadBean(path, "bean");

            for (RootPath rp : listRootPaths) {
                assert (!contains(rp));
                add(rp);
            }
        } catch (BeanXmlException e) {

            throw new OperationFailedException(
                    String.format("An error occurred loading bean XML from %s", path), e);
        }
    }

    private void add(RootPath item) throws OperationFailedException {

        if (contains(item)) {
            throw new OperationFailedException(
                    String.format("Item %s already exists in multi-map", item));
        }

        map.put(item.getName(), item);
    }

    /**
     * Finds a root to match a name and debug/status
     *
     * @param name this name must match
     * @param debug if true, priority is given to root paths which also match debug.
     * @return the found root, or an exception if one cannot be found
     * @throws AnchorIOException
     */
    @SuppressWarnings("unchecked")
    public RootPath findRoot(String name, boolean debug) throws AnchorIOException {

        Collection<RootPath> exst = (Collection<RootPath>) map.get(name);

        // We check both, as the behaviour is implementation-dependent in the multimap
        if (exst == null || exst.isEmpty()) {
            throw new AnchorIOException(String.format("Cannot find a root-path for '%s'", name));
        }

        // With the currently implementation there can be a maximum of two items with the same name
        // (with debug=true/false)
        assert (exst.size() < 3);
        if (exst.size() == 1) {
            // Always take the first one
            return exst.iterator().next();
        } else {
            // exst.size()==2

            // We look for the one that matches debug
            Iterator<RootPath> itr = exst.iterator();
            RootPath first = itr.next();

            if (first.isDebug() == debug) {
                return first;
            }

            return itr.next();
        }
    }

    /**
     * Is a RootPath contained within the map? (i.e. all properties on a RootPath match an existing
     * entry)
     *
     * @param item item to check
     * @return true if it is contained, false is if it not
     */
    private boolean contains(RootPath item) {
        @SuppressWarnings("unchecked")
        Collection<RootPath> exst = (Collection<RootPath>) map.get(item.getName());

        if (exst == null) {
            return false;
        }

        for (RootPath other : exst) {
            if (other.equals(item)) {
                return true;
            }
        }

        return false;
    }
}
