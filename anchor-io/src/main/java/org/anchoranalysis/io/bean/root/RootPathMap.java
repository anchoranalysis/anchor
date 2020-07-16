/* (C)2020 */
package org.anchoranalysis.io.bean.root;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.error.AnchorIOException;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

/**
 * A collection of RootPath objects indexed by their name.
 *
 * <p>Currently exists as a SINGLETON
 *
 * @author Owen Feehan
 */
public class RootPathMap {

    private static RootPathMap instance = null;

    private RootPathMap() {}

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

        SplitPath out = new SplitPath();

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
     * @param debug if TRUE, priority is given to root paths which also match debug.
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
        // (with debug=TRUE/FALSE)
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
     * @return TRUE if it is contained, FALSE is if it not
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
