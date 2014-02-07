package org.scigap.iucig.controller;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/application/")
public class ApplicationController {
    private final Logger logger = Logger.getLogger(getClass());

    @ResponseBody
    @RequestMapping(value = "/jobs/{jobID}/info", method = RequestMethod.GET)
    public String getDummyJob(@PathVariable(value = "jobID") final String jobID) throws IOException {

        Map<String, Object> job = new HashMap<String, Object>();
        job.put("id", jobID);
        job.put("name", "Airavata Tester");
        job.put("resource", "Big Red II");
        job.put("status", "Queued");
        job.put("createdDate", "01_14_2014");

        //todo : test if two input files contain the same name because Angular doesn't allow that.

        List<String> inputs = new ArrayList<String>();
        List<String> intermediateFiles = new ArrayList<String>();
        List<String> outputs = new ArrayList<String>();

        inputs.add("/Users/swithana/test.trt");
        inputs.add("test/lastInput.java");

        intermediateFiles.add("test/lastintermid.java");
        intermediateFiles.add("test/lastIntermid.class");

        outputs.add("test/output1.java");
        outputs.add("test/output2.csv");


        job.put("inputs", inputs);
        job.put("intermediateFiles", intermediateFiles);
        job.put("outputs", outputs);

        JSONObject job_json = new JSONObject(job);

        return job_json.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/allJobs", method = RequestMethod.GET)
    public String getAllJobs() throws IOException {
        Map<String, String> job3 = new HashMap<String, String>();
        job3.put("id", "j3");
        job3.put("name", "Job three");
        job3.put("machine", "Mason");
        job3.put("status", "Finished");
        job3.put("lastRunTime", "01232014");
        job3.put("project", "Airavata");
        job3.put("description", "This is a test description");

        Map<String, String> job4 = new HashMap<String, String>();
        job4.put("id", "j4");
        job4.put("name", "Job four");
        job4.put("machine", "Big Red II");
        job4.put("status", "Queued");
        job4.put("lastRunTime", "993399");
        job4.put("project", "Protein");
        job4.put("description", "Hello Test");

        Map<String, String> job5 = new HashMap<String, String>();
        job5.put("id", "j5");
        job5.put("name", "Test Experiment");
        job5.put("machine", "Quarry");
        job5.put("status", "Launched");
        job5.put("lastRunTime", "0123442014");
        job5.put("project", "Protein");
        job5.put("description", "Protein working test");

        Map<String, String> job6 = new HashMap<String, String>();
        job6.put("id", "j6");
        job6.put("name", "r2pg0-119 Exp");
        job6.put("machine", "Big Red II");
        job6.put("status", "Finished");
        job6.put("lastRunTime", "012332014");
        job6.put("project", "Cybergateway");
        job6.put("description", "Quary test project");

        Map<String, String> job7 = new HashMap<String, String>();
        job7.put("id", "j7");
        job7.put("name", "Airavata Tester");
        job7.put("machine", "Big Red II");
        job7.put("status", "Queued");
        job7.put("lastRunTime", "0123442014");
        job7.put("project", "Cybergateway");
        job7.put("description", "bigred 2 tester");

        JSONObject job3_json = new JSONObject(job3);
        JSONObject job4_json = new JSONObject(job4);
        JSONObject job5_json = new JSONObject(job5);
        JSONObject job6_json = new JSONObject(job6);
        JSONObject job7_json = new JSONObject(job7);
        JSONArray jsonArray = new JSONArray();

        jsonArray.add(job3_json);
        jsonArray.add(job4_json);
        jsonArray.add(job5_json);
        jsonArray.add(job6_json);
        jsonArray.add(job7_json);

        return jsonArray.toString();
    }

    @RequestMapping(value = "/jobs/{jobID}/{file_name}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getFile(@PathVariable("file_name") String fileName, @PathVariable("jobID") String jobID) {
        return new FileSystemResource(fileName);
    }

    private String checkFileUpload = "fileNotUploaded";

    @ResponseBody
    @RequestMapping(value = "/uploadPDB/{jobID}", headers = "content-type=multipart/*", method = RequestMethod.POST)
    public String uploadPDBfile(@PathVariable(value = "jobID") final String jobID,
                                @RequestParam("file") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File("testFileUpload.torrent")));
                stream.write(bytes);
                stream.close();
                checkFileUpload = "You successfully uploaded " + "-uploaded !";
            } catch (Exception e) {
                checkFileUpload = "You failed to upload " + e.getMessage();
            }
        } else {
            checkFileUpload = "You failed to upload " + " because the file was empty.";
        }
        return checkFileUpload;
    }

    @ResponseBody
    @RequestMapping(value = "/uploadPDB/{jobID}", method = RequestMethod.GET)
    public String fileUploadCheck(@PathVariable(value = "jobID") final String jobID) throws IOException {


        return checkFileUpload;
    }

    private void log(String message) {
        logger.info(message);
    }
}
