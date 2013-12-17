package org.scigap.iucig.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.scigap.iucig.gateway.util.BR2JobSubmission;
import org.scigap.iucig.gateway.util.JobParameters;
import org.scigap.iucig.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/job/")
public class JobSubmissionController {
	
	@Autowired
	UserService userService;

	private final Logger logger = Logger.getLogger(getClass());

    @ResponseBody
	@RequestMapping(value="bigred2/submit", method = RequestMethod.POST)
	public String submitBigred2Job(@RequestParam(value="application", required=false) String application, @RequestParam(value="jobname") String jobname,
			@RequestParam(value="jobtype") String jobtype, @RequestParam(value="workingdir") String workingdir,
			@RequestParam(value="executable") String executable, @RequestParam(value="inputfile") String inputfiles,
			@RequestParam(value="queuename", required=false) String queuename, @RequestParam(value="maxwalltime") String maxwalltime,
			@RequestParam(value="cpucount") Integer cpucount, @RequestParam(value="nodecount") Integer nodecount,
			@RequestParam(value="ppn", required=false) Integer processorPerNode, @RequestParam(value="minmemory", required=false) Integer minmemory,
			@RequestParam(value="maxmemory", required=false) Integer maxmemory) {
		logger.debug("Submitting a job on to BigRed II");
		JobParameters job = new JobParameters();
		job.setJobName(jobname);
		job.setWorkingDir(workingdir);
		job.setExecutablePath(executable);
		List<String> inputFileList = new ArrayList<String>();
		inputFileList.add(inputfiles);
		job.setInputFilePaths(inputFileList);
		job.setQueueName(queuename);
		job.setMaxWallTime(maxwalltime);
		job.setCpuCount(cpucount);
		job.setNodeCount(nodecount);
		job.setPpn(processorPerNode);
		job.setMinMemory(minmemory);
		job.setMaxMemory(maxmemory);
		BR2JobSubmission bigred2 = new BR2JobSubmission();
		String jobid = bigred2.submitJob(job, userService.getAuthenticatedUser().getUsername());
		return jobid;
	}
}
