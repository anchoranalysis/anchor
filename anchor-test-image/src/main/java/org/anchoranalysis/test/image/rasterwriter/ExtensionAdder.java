package org.anchoranalysis.test.image.rasterwriter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ExtensionAdder {

    public static String addExtension(String filename, String extensionToAdd) {
        return filename + "." + extensionToAdd;
    }
}
