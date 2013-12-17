package org.scigap.iucig.gateway.util;

import org.apache.airavata.gsi.ssh.api.Cluster;
import org.apache.airavata.gsi.ssh.api.SSHApiException;
import org.apache.airavata.gsi.ssh.api.ServerInfo;
import org.apache.airavata.gsi.ssh.api.authentication.AuthenticationInfo;
import org.apache.airavata.gsi.ssh.api.job.JobDescriptor;
import org.apache.airavata.gsi.ssh.impl.PBSCluster;
import org.apache.airavata.gsi.ssh.impl.authentication.DefaultPublicKeyAuthentication;
import org.apache.airavata.gsi.ssh.impl.authentication.DefaultPublicKeyFileAuthentication;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * This class is for submit jobs to BR2 using the portal
 */
public class BR2JobSubmission {

    public static final String BR2_HOST_NAME = "bigred2.uits.iu.edu";
    protected static Logger log = LoggerFactory.getLogger(BR2JobSubmission.class);

//    public static void main(String[] args) {
//        JobParameters jobParameters = new JobParameters();
//        jobParameters.setCpuCount(1);
//        jobParameters.setExecutablePath("/bin/echo");
//        jobParameters.setWorkingDir("/N/u/cpelikan/BigRed2/jobs");
//        jobParameters.setCpuCount(1);
//        jobParameters.setNodeCount(1);
//        jobParameters.setJobName("test");
//        jobParameters.setPpn(1);
//        jobParameters.setMaxWallTime("5");
//        List<String> inputs = new ArrayList<String>();
//        inputs.add("/N/u/cpelikan/BigRed2/jobs/hello.txt");
//        jobParameters.setInputFilePaths(inputs);
//        String jobID = submitJob(jobParameters, "cpelikan");
//        System.out.println("######## job id #########" + jobID);
//    }


    /**
     * Submit jobs
     * @param jobParameters job parameters
     * @param portalUser portal user
     * @return
     */
    public static String submitJob(JobParameters jobParameters, String portalUser) {
        try {
            AuthenticationInfo authInfo = getAuthInfo(portalUser);
            ServerInfo serverInfo = new ServerInfo(portalUser, BR2_HOST_NAME);

            Cluster pbsCluster = new PBSCluster(serverInfo, authInfo, "/opt/torque/torque-4.2.3.1/bin/");
            JobDescriptor jobDescriptor = new JobDescriptor();
            String workingDirectory = setUpWorkingDir(jobParameters.getWorkingDir(), jobParameters.getJobName());
            pbsCluster.makeDirectory(workingDirectory);
            Thread.sleep(1000);
            pbsCluster.makeDirectory(workingDirectory + File.separator + "inputs");
            Thread.sleep(1000);
            pbsCluster.makeDirectory(workingDirectory + File.separator +  "outputs");
            jobDescriptor.setWorkingDirectory(workingDirectory);
            jobDescriptor.setShellName("/bin/bash");
            jobDescriptor.setJobName(jobParameters.getJobName());
            jobDescriptor.setExecutablePath(jobParameters.getExecutablePath());
            jobDescriptor.setAllEnvExport(true);
            jobDescriptor.setMailOptions("n");
            jobDescriptor.setStandardOutFile(jobParameters.getWorkingDir() + File.separator + "application.out");
            jobDescriptor.setStandardErrorFile(jobParameters.getWorkingDir() + File.separator + "application.err");
            jobDescriptor.setNodes(1);
            jobDescriptor.setProcessesPerNode(jobParameters.getPpn());
            jobDescriptor.setQueueName(jobParameters.getQueueName());
            jobDescriptor.setMaxWallTime(jobParameters.getMaxWallTime());
            jobDescriptor.setJobSubmitter("aprun -n 1");
            jobDescriptor.setInputValues(jobParameters.getInputFilePaths());
            System.out.println(jobDescriptor.toXML());
            Thread.sleep(1000);
            pbsCluster.makeDirectory(workingDirectory + File.separator + "inputs");
            Thread.sleep(1000);
            pbsCluster.makeDirectory(workingDirectory + File.separator +  "outputs");
            log.info("Job descriptor: ", jobDescriptor.toXML());
            String jobID = pbsCluster.submitBatchJob(jobDescriptor);
            log.info("JobID returned : " + jobID);
            return jobID;

        } catch (SSHApiException e) {
            log.error("Unable to log in the BR2 using SSH keys", e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            log.error("Connection interrupted", e);
            e.printStackTrace();
        }
        return null;
    }

    public static String setUpWorkingDir (String workingDir, String jobName){
        String date = (new Date()).toString();
        date = date.replaceAll(" ", "_");
        date = date.replaceAll(":", "_");
        workingDir = workingDir + File.separator
                + jobName + "_" + date + "_" + UUID.randomUUID();
        return workingDir;
    }

    public static AuthenticationInfo getAuthInfo(String portalUser) {
        BR2Credential br2Credential = ACSUtils.readFromCredentialStore(portalUser);
        return new DefaultPublicKeyAuthentication(br2Credential.getPrivatekey(), br2Credential.getPubKey(), br2Credential.getPassphrase());
    }
}
