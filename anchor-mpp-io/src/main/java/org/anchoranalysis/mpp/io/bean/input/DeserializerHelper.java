/* (C)2020 */
package org.anchoranalysis.mpp.io.bean.input;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.io.bean.deserializer.XStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DeserializerHelper {

    private static XStreamDeserializer<Cfg> deserializerCfg = new XStreamDeserializer<>();
    private static XStreamDeserializer<MarkAnnotation> deserializerAnnotation =
            new XStreamDeserializer<>();

    public static Cfg deserializeCfg(Path path) throws DeserializationFailedException {
        return deserializerCfg.deserialize(path);
    }

    public static Cfg deserializeCfgFromAnnotation(
            Path outPath, boolean includeAccepted, boolean includeRejected)
            throws DeserializationFailedException {
        MarkAnnotation ann = deserializerAnnotation.deserialize(outPath);
        if (!ann.isFinished()) {
            throw new DeserializationFailedException("Annotation was never finished");
        }
        if (!ann.isAccepted()) {
            throw new DeserializationFailedException("Annotation was never accepted");
        }

        Cfg cfgOut = new Cfg();

        if (includeAccepted) {
            cfgOut.addAll(ann.getCfg());
        }

        if (includeRejected && ann.getCfgReject() != null) {
            cfgOut.addAll(ann.getCfgReject());
        }

        return cfgOut;
    }
}
