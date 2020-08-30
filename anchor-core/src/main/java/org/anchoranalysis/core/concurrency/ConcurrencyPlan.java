package org.anchoranalysis.core.concurrency;

/**
 * How many allocated CPUS and CPUs can be used.
 * 
 * @author Owen Feehan
 *
 */
public class ConcurrencyPlan {

    /** The total number of available CPUs processors */
    private final int totalNumberCPUs;
    
    /** The maximum number of available GPUs that can be used (to substitute for a CPU at places) */
    private final int numberGPUs;
    
    /**
     * Creates a plan for a single-CPU processor 
     * @return
     */
    public static ConcurrencyPlan singleProcessor(int maxNumberGPUs) {
        return new ConcurrencyPlan(1, maxNumberGPUs);
    }
    
    /**
     * Creates a plan for a multiple CPU-processors
     *  
     * @return
     */
    public static ConcurrencyPlan multipleProcessors(int maxNumberCPUs, int maxNumberGPUs) {
        return new ConcurrencyPlan(maxNumberCPUs, maxNumberGPUs);
    }
    
    public int totalNumber() {
        return totalNumberCPUs;
    }
    
    public int numberGPUs() {
        return numberGPUs;
    }
    
    public int totalMinusGPUs() {
        return totalNumberCPUs - numberGPUs;
    }
    
    private ConcurrencyPlan(int maxAvailableCPUs, int maxNumberGPUs) {
        this.totalNumberCPUs = Math.max(maxAvailableCPUs, maxNumberGPUs);
        this.numberGPUs = Math.min(maxNumberGPUs, maxAvailableCPUs);
    }
}
