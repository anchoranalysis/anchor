/*-
 * #%L
 * anchor-beans-shared
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
package org.anchoranalysis.bean.shared.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.bean.annotation.BeanField;

public class RegExList extends RegEx {

    // START BEAN PROPERTIES
    @BeanField private List<RegEx> list = new ArrayList<>();
    // END BEAN PROPERTIES

    @Override
    public Optional<String[]> match(String str) {

        for (RegEx re : list) {
            Optional<String[]> matches = re.match(str);
            if (matches.isPresent()) {
                return matches;
            }
        }
        return Optional.empty();
    }

    public List<RegEx> getList() {
        return list;
    }

    public void setList(List<RegEx> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (RegEx re : list) {
            sb.append(re.toString());

            if (first) {
                first = false;
            } else {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
