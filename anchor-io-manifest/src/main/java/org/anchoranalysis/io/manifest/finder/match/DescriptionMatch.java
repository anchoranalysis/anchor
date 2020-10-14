package org.anchoranalysis.io.manifest.finder.match;

import java.util.function.Predicate;
import org.anchoranalysis.io.manifest.ManifestDescription;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class DescriptionMatch {

    public static Predicate<ManifestDescription> functionAndType(String function, String type) {
        return function(function).and(type(type));
    }
        
    public static Predicate<ManifestDescription> eitherFunctionAndType(String function1, String function2, String type) {
        Predicate<ManifestDescription> functionMatch =
                DescriptionMatch.function(function1).or(
                        DescriptionMatch.function(function2)
        ); 
        return functionMatch.and(DescriptionMatch.type(type));        
    }
    
    private static Predicate<ManifestDescription> type(String type) {
        return other -> other.getType().equals(type);
    }
    
    public static Predicate<ManifestDescription> function(String function) {
        return other -> other.getFunction().equals(function);
    }
}
