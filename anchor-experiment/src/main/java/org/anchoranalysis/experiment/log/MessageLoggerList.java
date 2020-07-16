/* (C)2020 */
package org.anchoranalysis.experiment.log;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;

public class MessageLoggerList implements StatefulMessageLogger {

    private final List<StatefulMessageLogger> list;

    public MessageLoggerList(Stream<StatefulMessageLogger> stream) {
        list = stream.collect(Collectors.toList());
    }

    @Override
    public void start() {
        for (StatefulMessageLogger logger : list) {
            assert logger != null;
            logger.start();
        }
    }

    @Override
    public void log(String message) {
        for (StatefulMessageLogger logger : list) {
            assert logger != null;
            logger.log(message);
        }
    }

    @Override
    public void close(boolean successful) {
        for (StatefulMessageLogger logger : list) {
            logger.close(successful);
        }
    }

    @Override
    public void logFormatted(String formatString, Object... args) {
        for (MessageLogger logger : list) {
            logger.logFormatted(formatString, args);
        }
    }

    public boolean add(StatefulMessageLogger arg0) {
        return list.add(arg0);
    }

    public List<StatefulMessageLogger> getList() {
        return list;
    }
}
