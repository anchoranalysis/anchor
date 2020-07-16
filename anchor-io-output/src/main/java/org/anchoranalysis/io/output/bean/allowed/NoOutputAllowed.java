/* (C)2020 */
package org.anchoranalysis.io.output.bean.allowed;

public class NoOutputAllowed extends OutputAllowed {

    @Override
    public boolean isOutputAllowed(String outputName) {
        return false;
    }
}
