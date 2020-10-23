package org.anchoranalysis.io.generator.sequence;

import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor class AlternatingGenerator<T> implements Generator<T> {
    
    // START REQUIRED ARGUMENTS
    private final Generator<T> first;
    private final Generator<T> second;
    // END REQUIRED ARGUMENTS
    
    private int counter = 0;
    
    @Override
    public FileType[] write(T element, OutputNameStyle outputNameStyle,
            OutputterChecked outputter) throws OutputWriteFailedException {
        return selectAndIncrement().write(element, outputNameStyle, outputter);
    }
    @Override
    public FileType[] writeWithIndex(T element, String index,
            IndexableOutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        return selectAndIncrement().writeWithIndex(element, index, outputNameStyle, outputter);
    }
    
    private Generator<T> selectAndIncrement() {
        if ((counter++ % 2)==0) {
            return first;
        } else {
            return second;
        }
    }
}