package org.scigap.iucig.controller;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping(value="/amberCtrl/")
public class AmberController {
    private final Logger logger = Logger.getLogger(getClass());

    @ResponseBody
    @RequestMapping(value="/jobs/{jobID}", method = RequestMethod.GET)
    public String getDummyJob(@PathVariable(value="jobID") final String jobID) throws IOException {

        Map job = new HashMap();
        job.put("tleap_status", "Completed");
        job.put("amber_status", "Completed");
        job.put("postProcess_status", "Pending");
        job.put("tleap_inputFiles", "/Users/swithana");
        job.put("amber_inputFiles", "/Users/swithana");
        job.put("postProcess_inputFiles", "/Users/swithana");
        job.put("tleap_outputFiles", "/Users/swithana/output");
        job.put("amber_outputFiles", "/Users/swithana/output");
        job.put("postProcess_outputFiles", "/Users/swithana/output");

        JSONObject job_json = new JSONObject(job);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(job_json);

        String result = job_json.toString();
        return result;
    }

    @ResponseBody
    @RequestMapping(value="/allJobs", method = RequestMethod.GET)
    public String getAllJobs() throws IOException {
        Map<String, String> job3 = new HashMap<String, String>();
        job3.put("id", "j3");
        job3.put("name", "Job three");
        job3.put("machine", "Mason");
        job3.put("currentStep", "Amber");
        job3.put("lastRunTime", "01232014");

        Map<String, String> job4 = new HashMap<String, String>();
        job4.put("id", "j4");
        job4.put("name", "Job four");
        job4.put("machine", "Big Red II");
        job4.put("currentStep", "Tleap");
        job4.put("lastRunTime", "993399");


        Map<String, String> job5 = new HashMap<String, String>();
        job5.put("id", "j5");
        job5.put("name", "Test Experiment");
        job5.put("machine", "Quarry");
        job5.put("currentStep", "PostProcess");
        job5.put("lastRunTime", "0123442014");

        Map<String, String> job6 = new HashMap<String, String>();
        job6.put("id", "j6");
        job6.put("name", "r2pg0-119 Exp");
        job6.put("machine", "Big Red II");
        job6.put("currentStep", "PostProcess");
        job6.put("lastRunTime", "012332014");

        Map<String, String> job7 = new HashMap<String, String>();
        job7.put("id", "j7");
        job7.put("name", "Airavata Tester");
        job7.put("machine", "Big Red II");
        job7.put("currentStep", "Amber");
        job7.put("lastRunTime", "0123442014");

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

    private String checkFileUpload = "fileNotUploaded";
    @ResponseBody
    @RequestMapping(value="/uploadPDB/{jobID}", headers = "content-type=multipart/*",method = RequestMethod.POST)
    public String uploadPDBfile(@PathVariable(value="jobID") final String jobID,
                                @RequestParam("file") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File( "testFileUpload.torrent")));
                stream.write(bytes);
                stream.close();
                checkFileUpload = "You successfully uploaded " +"-uploaded !";
            } catch (Exception e) {
                checkFileUpload= "You failed to upload " + e.getMessage();
            }
        } else {
            checkFileUpload =  "You failed to upload " + " because the file was empty.";
        }
        return checkFileUpload;
    }

    @ResponseBody
    @RequestMapping(value="/uploadPDB/{jobID}", method = RequestMethod.GET)
    public String fileUploadCheck(@PathVariable(value="jobID") final String jobID) throws IOException {


        return checkFileUpload;
    }
    private void log(String message){
        logger.info(message);
    }
}
