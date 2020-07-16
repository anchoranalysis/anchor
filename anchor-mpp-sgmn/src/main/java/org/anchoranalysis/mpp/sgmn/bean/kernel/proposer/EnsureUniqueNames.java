/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.kernel.proposer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.WeightedKernel;

class EnsureUniqueNames {

    private EnsureUniqueNames() {}

    public static <T> void apply(List<WeightedKernel<T>> lstKernelFactories) {
        Map<String, Integer> hashName = cntUniqueNames(lstKernelFactories);
        appendIntegerIfNecessary(lstKernelFactories, hashName);
    }

    private static <T> Map<String, Integer> cntUniqueNames(
            List<WeightedKernel<T>> lstKernelFactories) {

        HashMap<String, Integer> hashName = new HashMap<>();

        for (WeightedKernel<?> wkf : lstKernelFactories) {

            String name = wkf.getKernel().getBeanName();
            if (hashName.get(name) == null) {
                hashName.put(name, 1);
            } else {
                int val = hashName.get(name);
                hashName.put(name, ++val);
            }
        }

        return hashName;
    }

    // Now append an integer index to each kernel name that appears multiple times
    private static <T> void appendIntegerIfNecessary(
            List<WeightedKernel<T>> lstKernelFactories, Map<String, Integer> hashName) {
        HashMap<String, Integer> hashRunning = new HashMap<>();

        for (WeightedKernel<?> wkf : lstKernelFactories) {

            String name = wkf.getKernel().getBeanName();

            int valTotal = hashName.get(name);
            assert valTotal > 0;

            if (valTotal == 1) {
                // do nothing as there is only a single instance of the name
            } else {

                int indexToAppend;
                if (hashRunning.get(name) == null) {
                    hashRunning.put(name, 0);
                    indexToAppend = 0;
                } else {
                    int valRunning = hashRunning.get(name);
                    hashRunning.put(name, ++valRunning);
                    indexToAppend = valRunning;
                }

                String newName = String.format("%s_%d", name, indexToAppend);
                wkf.setName(newName);
            }
        }
    }
}
