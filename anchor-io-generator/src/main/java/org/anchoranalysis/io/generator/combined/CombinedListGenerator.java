/* (C)2020 */
package org.anchoranalysis.io.generator.combined;

import java.util.ArrayList;
import java.util.Optional;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.MultipleFileTypeGenerator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

// currently untested
public class CombinedListGenerator implements MultipleFileTypeGenerator {

    private ArrayList<OptionalNameValue<Generator>> list = new ArrayList<>();

    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings) {

        ArrayList<FileType> all = new ArrayList<>();

        for (OptionalNameValue<Generator> ni : list) {
            Optional<FileType[]> arr = ni.getValue().getFileTypes(outputWriteSettings);
            arr.ifPresent(
                    a -> {
                        for (int i = 0; i < a.length; i++) {
                            all.add(a[i]);
                        }
                    });
        }

        if (!all.isEmpty()) {
            return Optional.of(all.toArray(new FileType[] {}));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void write(OutputNameStyle outputNameStyle, BoundOutputManager outputManager)
            throws OutputWriteFailedException {

        for (OptionalNameValue<Generator> ni : list) {
            ni.getName().ifPresent(outputNameStyle::setOutputName);
            ni.getValue().write(outputNameStyle, outputManager);
        }
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            String index,
            BoundOutputManager outputManager)
            throws OutputWriteFailedException {

        int maxWritten = -1;
        for (OptionalNameValue<Generator> ni : list) {

            if (ni.getName().isPresent()) {
                outputNameStyle = outputNameStyle.duplicate();
                outputNameStyle.setOutputName(ni.getName().get());
            }

            int numWritten = ni.getValue().write(outputNameStyle, index, outputManager);
            maxWritten = Math.max(maxWritten, numWritten);
        }

        return maxWritten;
    }

    /**
     * Adds a generator with an optional-name.
     *
     * <p>Note that everything should have a name, or nothing should. Please don't mix. This is not
     * currently checked.
     *
     * @param generator the generator to add
     * @param name optional-name, which if included, is set as the output-name for the generator
     */
    public void add(Generator generator, Optional<String> name) {
        list.add(new OptionalNameValue<>(name, generator));
    }
}
