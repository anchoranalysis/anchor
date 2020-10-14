package org.anchoranalysis.io.generator.combined;

import java.io.Serializable;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.generator.MultipleFileTypeGenerator;
import org.anchoranalysis.io.generator.serialized.ObjectOutputStreamGenerator;
import org.anchoranalysis.io.generator.serialized.XStreamGenerator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

public class ManifestGenerator<T extends Serializable> implements MultipleFileTypeGenerator<T> {

    private final CombinedListGenerator<T> delegate;
    
    public ManifestGenerator() {
        delegate = new CombinedListGenerator<>(new XStreamGenerator<>(), new ObjectOutputStreamGenerator<>());
    }

    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException {
        return delegate.getFileTypes(outputWriteSettings);
    }

    @Override
    public void write(T element, OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        delegate.write(element, outputNameStyle, outputter);
    }

    @Override
    public int writeWithIndex(T element, String index, IndexableOutputNameStyle outputNameStyle,
            OutputterChecked outputter) throws OutputWriteFailedException {
        return delegate.writeWithIndex(element, index, outputNameStyle, outputter);
    }

}
