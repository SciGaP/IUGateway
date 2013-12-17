package org.scigap.iucig.gateway.util;


import java.util.List;

/**
 * This is the basic class to capture properties of a job
 */
public class JobParameters {
    private String workingDir;
    private String jobName = "normal";
    private String executablePath;
    private Integer ppn = 1;
    private String queueName = "normal";
    private String maxWallTime = "5";
    private Integer cpuCount = 1;
    private Integer nodeCount = 1;
    private Integer minMemory = 1;
    private Integer maxMemory = 1;
    private List<String> inputFilePaths;

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public int getPpn() {
        return ppn;
    }

    public void setPpn(Integer ppn) {
    	if(ppn!=null)
    		this.ppn = ppn;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        if (queueName != null){
            this.queueName = queueName;
        }
    }

    public String getMaxWallTime() {
        return maxWallTime;
    }

    public void setMaxWallTime(String maxWallTime) {
        this.maxWallTime = maxWallTime;
    }

    public int getCpuCount() {
        return cpuCount;
    }

    public void setCpuCount(Integer cpuCount) {
    	if(cpuCount!=null)
    		this.cpuCount = cpuCount;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(Integer nodeCount) {
    	if(nodeCount!=null)
    		this.nodeCount = nodeCount;
    }

    public int getMinMemory() {
        return minMemory;
    }

    public void setMinMemory(Integer minMemory) {
        if(minMemory!=null)
        	this.minMemory = minMemory;
    }

    public int getMaxMemeory() {
        return maxMemory;
    }

    public void setMaxMemory(Integer maxMemory) {
    	if(maxMemory!=null)
    		this.maxMemory = maxMemory;
    }

    public List<String> getInputFilePaths() {
        return inputFilePaths;
    }

    public void setInputFilePaths(List<String> inputFilePaths) {
        this.inputFilePaths = inputFilePaths;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getExecutablePath() {
        return executablePath;
    }

    public void setExecutablePath(String executablePath) {
        this.executablePath = executablePath;
    }
}
