package org.scigap.iucig.controller;
/*package iu.ogce.science.gateway.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/machineInfo/")
public class MachineSummaryController {
    private final Logger logger = Logger.getLogger(getClass());


    @Value("${bigred.summary.filename}")
    private String BIGRED_SUMMARY_FILE;

    *//**
     * Returns summary of jobs and nodes in Big Red
     *//*
    @ResponseBody
    @RequestMapping(value = "/bigred", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public String getBigRedSummary(HttpServletResponse response) {
        logger.debug("Getting Big Red node and job info");

        StringBuilder buffer = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
            		getClass().getClassLoader().getResourceAsStream(BIGRED_SUMMARY_FILE)));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                buffer.append(strLine);
            }
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            response.setStatus(HttpStatus.METHOD_FAILURE.value());
            return e.getMessage();
        }
        return buffer.toString();
    }

}
*/