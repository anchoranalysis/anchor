package org.anchoranalysis.io.output.recorded;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Like {@link RecordedOutputs} but accepts two levels, first and second, like in a {@link MultiLevelRecordedOutputs}.
 * 
 * @author Owen Feehan
 *
 */
@Accessors(fluent=true)
public class MultiLevelRecordedOutputs {
    
    /** For recording first-level outputs. */
    @Getter private RecordedOutputs first = new RecordedOutputs();

    /** A hash-map recording second-level outputs. */
    private HashMap<String,RecordedOutputs> second = new HashMap<>();
    
    /**
     * A {@link RecordedOutputs} for recording second-level outputs for a given {@code outputName} from the first-level.
     * 
     * @param outputName the outputName from the first-level
     * @return
     */
    public RecordedOutputs second(String outputName) {
        
        if (!first.isRecordedAsEnabled(outputName)) {
            throw new AnchorFriendlyRuntimeException("output-name has been recorded as a second-level output, without having being recorded as a first-level output: " + outputName);
        }
        
        return second.computeIfAbsent(outputName, name -> new RecordedOutputs() );
    }

    /**
     * All second-level recorded output entries.
     * 
     * @return the set of entries from the internal hash-map that records outputs.
     */
    public Set<Entry<String, RecordedOutputs>> secondEntries() {
        return second.entrySet();
    }
    
    /**
     * If there is at least one disabled output.
     * 
     * @return true if there is at least one output-name exists that was disabled.
     */
    public boolean hasAtLeastOneDisabled() {
        
        if (first.hasAtLeastOneDisabled()) {
            return true;
        }
        
        for(Entry<String, RecordedOutputs> entry : second.entrySet()) {
            if (entry.getValue().hasAtLeastOneDisabled()) {
                return true;
            }
        }
        
        return false;
    }
}
