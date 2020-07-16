/* (C)2020 */
package org.anchoranalysis.mpp.io.cfg;

import java.nio.file.Path;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.bean.deserializer.XStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;

public class CfgDeserializer implements Deserializer<Cfg> {

    private XStreamDeserializer<Cfg> delegate;

    public CfgDeserializer() {
        super();
        delegate = new XStreamDeserializer<>();
    }

    @Override
    public Cfg deserialize(Path filePath) throws DeserializationFailedException {
        return delegate.deserialize(filePath);
    }
}
