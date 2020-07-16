/* (C)2020 */
package org.anchoranalysis.io.bean.provider.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.IOFileFilter;

class WalkToDepth extends DirectoryWalker<File> {

    private static class RejectAllFiles implements IOFileFilter {

        @Override
        public boolean accept(File file) {
            return true;
        }

        @Override
        public boolean accept(File dir, String name) {
            return true;
        }
    }

    private int exactDepth;
    private ProgressReporterMultiple prm;

    public WalkToDepth(int exactDepth, ProgressReporterMultiple prm) {
        super(null, new RejectAllFiles(), exactDepth);
        this.exactDepth = exactDepth;
        this.prm = prm;
    }

    public List<File> findDirs(File root) throws IOException {
        List<File> results = new ArrayList<>();
        walk(root, results);
        return results;
    }

    @Override
    protected void handleFile(File file, int depth, Collection<File> results) throws IOException {
        // NOTHING TO DO
    }

    @Override
    protected boolean handleDirectory(File directory, int depth, Collection<File> results) {

        if (depth == 1) {
            prm.incrWorker();
        }

        if (depth == exactDepth) {
            results.add(directory);
        }

        return depth <= exactDepth;
    }
}
