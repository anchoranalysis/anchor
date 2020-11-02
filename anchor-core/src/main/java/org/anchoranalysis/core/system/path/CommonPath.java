package org.anchoranalysis.core.system.path;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.anchoranalysis.core.functional.FunctionalList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Finds the common root directory of a set of paths.
 * 
 * <p>This is derived from an example on <a href="http://rosettacode.org/wiki/Find_common_directory_path#Java">Rosetta Code</a>.
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class CommonPath {
    
    public static String commonPath(Collection<File> files) throws IOException{
        List<String> paths = FunctionalList.mapToList(files, IOException.class, file -> 
            FilePathToUnixStyleConverter.toStringUnixStyle( file.getCanonicalPath() )
        );
        return commonPath(paths);
    }
    
    private static String commonPath(List<String> paths){
        StringBuilder commonPath = new StringBuilder();
        
        String[][] folders = new String[paths.size()][];
        for(int i = 0; i < paths.size(); i++){
            folders[i] = paths.get(i).split("/"); //split on file separator
        }
        for(int j = 0; j < folders[0].length; j++){
            String thisFolder = folders[0][j]; //grab the next folder name in the first path
            boolean allMatched = true; //assume all have matched in case there are no more paths
            for(int i = 1; i < folders.length && allMatched; i++){ //look at the other paths
                if(folders[i].length < j){ //if there is no folder here
                    allMatched = false; //no match
                    break; //stop looking because we've gone as far as we can
                }
                //otherwise
                allMatched &= folders[i][j].equals(thisFolder); //check if it matched
            }
            if(allMatched){ //if they all matched this folder name
                //add it to the answer
                commonPath.append(thisFolder);
                commonPath.append("/");
            }else{//otherwise
                break;//stop looking
            }
        }
        return commonPath.toString();
    }
}
