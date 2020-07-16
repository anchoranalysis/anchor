/* (C)2020 */
package org.anchoranalysis.experiment.bean.io;

import java.util.Enumeration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class UpdateLog4JOutputManager {

    @SuppressWarnings("unchecked")
    public static void updateLog4J(BoundOutputManagerRouteErrors outputManager) {
        // Bit of reflection trickery involved

        Enumeration<Appender> enumApp = LogManager.getRootLogger().getAllAppenders();
        while (enumApp.hasMoreElements()) {

            try {

                Appender appender = enumApp.nextElement();

                if (appender instanceof Log4JFileAppender) {
                    Log4JFileAppender logjApp = (Log4JFileAppender) appender;

                    String outputName = logjApp.getOutputName();
                    if (!outputManager.isOutputAllowed(outputName)) {
                        LogManager.getRootLogger().removeAppender(appender);
                        continue;
                    }

                    logjApp.init(outputManager);
                    logjApp.activateOptions();
                }

            } catch (Exception e) {
                // Do nothing, and continue in loop
            }
        }
    }
}
