/* (C)2020 */
package org.anchoranalysis.image.io.chnl.map;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;
import org.anchoranalysis.image.io.bean.channel.map.ImgChnlMapEntry;

/**
 * A map of image channels to indices.
 *
 * <p>It is vital that the insertion order is preserved, so a LinkedHashMap or similar should be
 * used This bean has a custom-factory
 *
 * @author Owen Feehan
 */
public class ImgChnlMap {

    private LinkedHashMap<String, ImgChnlMapEntry> map = new LinkedHashMap<>();

    public void add(ImgChnlMapEntry entry) {
        map.put(entry.getName(), entry);
    }

    public int get(String name) {
        ImgChnlMapEntry entry = map.get(name);
        if (entry != null) {
            return entry.getIndex();
        } else {
            return -1;
        }
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Collection<ImgChnlMapEntry> entryCollection() {
        return map.values();
    }

    public int getException(String name) {
        int ind = get(name);
        if (ind != -1) {
            return ind;
        } else {
            throw new IndexOutOfBoundsException(
                    String.format("No channel index for '%s' in imgChnlMap", name));
        }
    }
}
