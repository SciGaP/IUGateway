package org.scigap.iucig.controller;

import java.io.*;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scigap.iucig.service.MWSService;
import org.scigap.iucig.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/jobInfo/")
public class JobInfoController {
	
	@Autowired
	MWSService service;
	
	@Autowired
	UserService userService;
	
	@Value("${quarry.mws.url}")
    private String QUARRY_MWS_URL;

    @Value("${br2.mws.url}")
    private String BIGRED2_MWS_URL;

    @Value("${mason.mws.url}")
    private String MASON_MWS_URL;
	
	private final Logger logger = Logger.getLogger(getClass());
	
	/** Returns All Running Jobs of all users in Quarry */
	@ResponseBody
	@RequestMapping(value="/quarry", method = RequestMethod.GET)
	public String getAllJobs(HttpServletResponse response) {
		HttpRequestBase mwsRequest = new HttpGet(QUARRY_MWS_URL +"jobs?api-version=2");
		logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
		return service.getResponseForRequest(mwsRequest,response);
	}

	/** Returns All Running Jobs of logged in user in Quarry */
	@ResponseBody
	@RequestMapping(value="/quarry/user", method = RequestMethod.GET)
	public String getQuarryUserJobs(HttpServletResponse response) {
		String username = userService.getAuthenticatedUser().getUsername();
		// Trying to encode URL. doesnt work as expected.
		//String encodedQuery = URLEncoder.encode("api-version=2&query={'credentials.user':'"+username+"'}", "UTF-8");
		// Manually encoding only the curly braces. { - %7B, } - %7D
		HttpRequestBase mwsRequest = new HttpGet(QUARRY_MWS_URL +"jobs?api-version=2&query=%7B'credentials.user':'"+username+"'%7D");
		logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
		return service.getResponseForRequest(mwsRequest,response); 
	}
	
	/** Returns the details of a particular job in Quarry 
	 * @throws UnsupportedEncodingException */
	@ResponseBody
	@RequestMapping(value="/quarry/{jobid}", method = RequestMethod.GET)
	public String getJobDetails(@PathVariable(value="jobid") final String jobid, HttpServletResponse response) throws UnsupportedEncodingException {
		String encodedJobID = URLEncoder.encode(jobid,"ISO-8859-1"); 
		HttpRequestBase mwsRequest = new HttpGet(QUARRY_MWS_URL +"jobs/"+encodedJobID+"?api-version=2");
		logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
		return service.getResponseForRequest(mwsRequest,response);
	}

    @ResponseBody
    @RequestMapping(value="/quarry/jobstatus")
    public String getJobStatus(HttpServletResponse response) {
        HttpRequestBase mwsRequest = new HttpGet(QUARRY_MWS_URL +"jobs?api-version=2&fields=name,states.state");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest,response);
    }
	
	/** Cancel a job
	 * @throws UnsupportedEncodingException */
	@ResponseBody
	@RequestMapping(value="/quarry/{jobid}", method = RequestMethod.DELETE)
	public String cancelJob(@PathVariable(value="jobid") final String jobid, HttpServletResponse response) throws UnsupportedEncodingException {
		try {
			String jobDetails = this.getJobDetails(jobid, response);
			JSONObject job = (JSONObject)new JSONParser().parse(jobDetails);
			JSONObject credentials = (JSONObject) job.get("credentials");
			String username = (String) credentials.get("user");
			if(userService.getAuthenticatedUser().getUsername().equals(username)) {
				String encodedJobID = URLEncoder.encode(jobid,"ISO-8859-1");
				HttpRequestBase mwsRequest = new HttpDelete(QUARRY_MWS_URL +"jobs/"+encodedJobID+"?api-version=2");
				logger.debug("Executing REST DELETE request" + mwsRequest.getRequestLine());
				String deleteResponse = service.getResponseForRequest(mwsRequest,response);
				if(deleteResponse.equals("{}")) {
					logger.debug("Delete successfull");
					response.setStatus(HttpStatus.OK.value());
					return "Delete successfull";
				} else {
					return deleteResponse;
				}
			} else {
				logger.error("Not permitted to delete other users Job");
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return "Not permitted to delete other users Job";
			}
		} catch (ParseException e) {
			logger.error("Error verifying user authorization");
			response.setStatus(HttpStatus.METHOD_FAILURE.value());
			return "Error verifying user authorization";
		}
	}
	
	/** Modify a job (hold/unhold/cancel/checkpoint/execute/requeue/resume/suspend/unhold)
	 * Refer MWS (Viewpoint) reference guide for list of supported operations
	 * @throws UnsupportedEncodingException */
	@ResponseBody
	@RequestMapping(value="/quarry/{jobid}/{operation}", method = RequestMethod.PUT)
	public String modifyJob(@PathVariable(value="jobid") final String jobid,@PathVariable(value="operation") final String operation, HttpServletResponse response) throws UnsupportedEncodingException {
		try {
			String jobDetails = this.getJobDetails(jobid, response);
			JSONObject job = (JSONObject)new JSONParser().parse(jobDetails);
			JSONObject credentials = (JSONObject) job.get("credentials");
			String username = (String) credentials.get("user");
			if(userService.getAuthenticatedUser().getUsername().equals(username)) {
				String encodedJobID = URLEncoder.encode(jobid,"ISO-8859-1");
				HttpRequestBase mwsRequest = new HttpPut(QUARRY_MWS_URL +"jobs/"+getQueryForModifyRequest(encodedJobID,operation));
				logger.info("Executing REST PUT request" + mwsRequest.getRequestLine());
				return service.getResponseForRequest(mwsRequest,response);
			} else {
				logger.error("Not permitted to modify other users Job");
				response.setStatus(HttpStatus.FORBIDDEN.value());
				return "Forbidden";
			}
		} catch (ParseException e) {
			logger.error("Error verifying user authorization");
			response.setStatus(HttpStatus.METHOD_FAILURE.value());
			return "Error verifying user authorization";
		}
	}

    @ResponseBody
    @RequestMapping(value="/mason" , method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public String getAllJobsMason(HttpServletResponse response) {
        HttpRequestBase mwsRequest = new HttpGet(MASON_MWS_URL +"jobs?api-version=2");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest,response);
    }

    /** Returns All Running Jobs of logged in user in Mason*/
    @ResponseBody
    @RequestMapping(value="/mason/user", method = RequestMethod.GET)
    public String getMasonUserJobs(HttpServletResponse response) {
        String username = userService.getAuthenticatedUser().getUsername();
        // Trying to encode URL. doesnt work as expected.
        //String encodedQuery = URLEncoder.encode("api-version=2&query={'credentials.user':'"+username+"'}", "UTF-8");
        // Manually encoding only the curly braces. { - %7B, } - %7D
        HttpRequestBase mwsRequest = new HttpGet(MASON_MWS_URL +"jobs?api-version=2&query=%7B'credentials.user':'"+username+"'%7D");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }

    /** Returns the details of a particular job
     * @throws UnsupportedEncodingException */
    @ResponseBody
    @RequestMapping(value="/mason/{jobid}", method = RequestMethod.GET)
    public String getMasonJobDetails(@PathVariable(value="jobid") final String jobid, HttpServletResponse response) throws UnsupportedEncodingException {
        String encodedJobID = URLEncoder.encode(jobid,"ISO-8859-1");
        HttpRequestBase mwsRequest = new HttpGet(MASON_MWS_URL +"jobs/"+encodedJobID+"?api-version=2");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }

    @ResponseBody
    @RequestMapping(value="/mason/jobstatus")
    public String getMasonJobStatus(HttpServletResponse response) {
        HttpRequestBase mwsRequest = new HttpGet(MASON_MWS_URL +"jobs?api-version=2&fields=name,states.state");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }

    /** Cancel a job
     * @throws UnsupportedEncodingException */
    @ResponseBody
    @RequestMapping(value="/mason/{jobid}", method = RequestMethod.DELETE)
    public String masoncancelJob(@PathVariable(value="jobid") final String jobid, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            String jobDetails = this.getJobDetails(jobid, response);
            JSONObject job = (JSONObject)new JSONParser().parse(jobDetails);
            JSONObject credentials = (JSONObject) job.get("credentials");
            String username = (String) credentials.get("user");
            if(userService.getAuthenticatedUser().getUsername().equals(username)) {
                String encodedJobID = URLEncoder.encode(jobid,"ISO-8859-1");
                HttpRequestBase mwsRequest = new HttpDelete(MASON_MWS_URL +"jobs/"+encodedJobID+"?api-version=2");
                logger.debug("Executing REST DELETE request" + mwsRequest.getRequestLine());
                String deleteResponse = service.getResponseForRequest(mwsRequest, response);
                if(deleteResponse.equals("{}")) {
                    logger.debug("Delete successfull");
                    response.setStatus(HttpStatus.OK.value());
                    return "Delete successfull";
                } else {
                    return deleteResponse;
                }
            } else {
                logger.error("Not permitted to delete other users Job");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return "Not permitted to delete other users Job";
            }
        } catch (ParseException e) {
            logger.error("Error verifying user authorization");
            response.setStatus(HttpStatus.METHOD_FAILURE.value());
            return "Error verifying user authorization";
        }
    }

    /** Modify a job (hold/unhold/cancel/checkpoint/execute/requeue/resume/suspend/unhold)
     * Refer MWS (Viewpoint) reference guide for list of supported operations
     * @throws UnsupportedEncodingException */
    @ResponseBody
    @RequestMapping(value="/mason/{jobid}/{operation}", method = RequestMethod.PUT)
    public String modifyJobMason(@PathVariable(value="jobid") final String jobid,@PathVariable(value="operation") final String operation, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            String jobDetails = this.getJobDetails(jobid, response);
            JSONObject job = (JSONObject)new JSONParser().parse(jobDetails);
            JSONObject credentials = (JSONObject) job.get("credentials");
            String username = (String) credentials.get("user");
            if(userService.getAuthenticatedUser().getUsername().equals(username)) {
                String encodedJobID = URLEncoder.encode(jobid,"ISO-8859-1");
                HttpRequestBase mwsRequest = new HttpPut(MASON_MWS_URL +"jobs/"+getQueryForModifyRequest(encodedJobID,operation));
                logger.debug("Executing REST PUT request" + mwsRequest.getRequestLine());
                return service.getResponseForRequest(mwsRequest, response);
            } else {
                logger.error("Not permitted to delete other users Job");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return "Forbidden";
            }
        } catch (ParseException e) {
            logger.error("Error verifying user authorization");
            response.setStatus(HttpStatus.METHOD_FAILURE.value());
            return "Error verifying user authorization";
        }
    }

    /** Returns All Running Jobs of all users*/
    @ResponseBody
    @RequestMapping(value="/bigred2", method = RequestMethod.GET)
    public String getBR2AllJobs(HttpServletResponse response) {
        HttpRequestBase mwsRequest = new HttpGet(BIGRED2_MWS_URL +"jobs?api-version=2");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }

    /** Returns All Running Jobs of logged in user in Big Red II*/
    @ResponseBody
    @RequestMapping(value="/bigred2/user", method = RequestMethod.GET)
    public String getBR2UserJobs(HttpServletResponse response) {
        String username = userService.getAuthenticatedUser().getUsername();
        // Trying to encode URL. doesnt work as expected.
        //String encodedQuery = URLEncoder.encode("api-version=2&query={'credentials.user':'"+username+"'}", "UTF-8");
        // Manually encoding only the curly braces. { - %7B, } - %7D
        HttpRequestBase mwsRequest = new HttpGet(BIGRED2_MWS_URL +"jobs?api-version=2&query=%7B'credentials.user':'"+username+"'%7D");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }

    /** Returns the details of a particular job
     * @throws UnsupportedEncodingException */
    @ResponseBody
    @RequestMapping(value="/bigred2/{jobid}", method = RequestMethod.GET)
    public String getBR2JobDetails(@PathVariable(value="jobid") final String jobid, HttpServletResponse response) throws UnsupportedEncodingException {
        String encodedJobID = URLEncoder.encode(jobid,"ISO-8859-1");
        HttpRequestBase mwsRequest = new HttpGet(BIGRED2_MWS_URL +"jobs/"+encodedJobID+"?api-version=2");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }

    @ResponseBody
    @RequestMapping(value="/bigred2/jobstatus")
    public String getBR2JobStatus(HttpServletResponse response) {
        HttpRequestBase mwsRequest = new HttpGet(BIGRED2_MWS_URL +"jobs?api-version=2&fields=name,states.state");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }

    /** Cancel a job
     * @throws UnsupportedEncodingException */
    @ResponseBody
    @RequestMapping(value="/bigred2/{jobid}", method = RequestMethod.DELETE)
    public String br2cancelJob(@PathVariable(value="jobid") final String jobid, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            String jobDetails = this.getJobDetails(jobid, response);
            JSONObject job = (JSONObject)new JSONParser().parse(jobDetails);
            JSONObject credentials = (JSONObject) job.get("credentials");
            String username = (String) credentials.get("user");
            if(userService.getAuthenticatedUser().getUsername().equals(username)) {
                String encodedJobID = URLEncoder.encode(jobid,"ISO-8859-1");
                HttpRequestBase mwsRequest = new HttpDelete(BIGRED2_MWS_URL +"jobs/"+encodedJobID+"?api-version=2");
                logger.debug("Executing REST DELETE request" + mwsRequest.getRequestLine());
                String deleteResponse = service.getResponseForRequest(mwsRequest, response);
                if(deleteResponse.equals("{}")) {
                    logger.debug("Delete successfull");
                    response.setStatus(HttpStatus.OK.value());
                    return "Delete successfull";
                } else {
                    return deleteResponse;
                }
            } else {
                logger.error("Not permitted to delete other users Job");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return "Not permitted to delete other users Job";
            }
        } catch (ParseException e) {
            logger.error("Error verifying user authorization");
            response.setStatus(HttpStatus.METHOD_FAILURE.value());
            return "Error verifying user authorization";
        }
    }

    /** Modify a job (hold/unhold/cancel/checkpoint/execute/requeue/resume/suspend/unhold)
     * Refer MWS (Viewpoint) reference guide for list of supported operations
     * @throws UnsupportedEncodingException */
    @ResponseBody
    @RequestMapping(value="/bigred2/{jobid}/{operation}", method = RequestMethod.PUT)
    public String modifyJobBr2(@PathVariable(value="jobid") final String jobid,@PathVariable(value="operation") final String operation, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            String jobDetails = this.getJobDetails(jobid, response);
            JSONObject job = (JSONObject)new JSONParser().parse(jobDetails);
            JSONObject credentials = (JSONObject) job.get("credentials");
            String username = (String) credentials.get("user");
            if(userService.getAuthenticatedUser().getUsername().equals(username)) {
                String encodedJobID = URLEncoder.encode(jobid,"ISO-8859-1");
                HttpRequestBase mwsRequest = new HttpPut(BIGRED2_MWS_URL +"jobs/"+getQueryForModifyRequest(encodedJobID,operation));
                logger.debug("Executing REST PUT request" + mwsRequest.getRequestLine());
                return service.getResponseForRequest(mwsRequest, response);
            } else {
                logger.error("Not permitted to delete other users Job");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return "Forbidden";
            }
        } catch (ParseException e) {
            logger.error("Error verifying user authorization");
            response.setStatus(HttpStatus.METHOD_FAILURE.value());
            return "Error verifying user authorization";
        }
    }
	
	private String getQueryForModifyRequest(String encodedJobID, String operation) {
		// Manually encoding only the curly braces. { - %7B, } - %7D, [ - %5B, ] - %5D
		if(operation.equals("hold") || operation.equals("unhold"))
			return encodedJobID+"?api-version=2&query=%7B'holds':%5B'User'%5D%7D";
		else
			return encodedJobID+"?api-version=2";
	}
}
