/* (C)2020 */
package org.anchoranalysis.io.output.file;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.anchoranalysis.io.error.AnchorIOException;

public class FileOutput {

    private final String filePath;

    // Files for writing out
    private PrintWriter out;

    public FileOutput(String filePath) {
        super();
        this.filePath = filePath;
    }

    public void start() throws AnchorIOException {
        try {
            FileWriter fileWriter = new FileWriter(filePath);

            this.out = new PrintWriter(fileWriter);
        } catch (IOException e) {
            throw new AnchorIOException("Cannot create file-writer", e);
        }
    }

    public boolean isEnabled() {
        return (out != null);
    }

    public PrintWriter getWriter() {
        return this.out;
    }

    public void end() {
        if (out != null) {
            out.close();
        }
    }
}
