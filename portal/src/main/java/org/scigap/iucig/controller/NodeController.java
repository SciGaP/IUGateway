package org.scigap.iucig.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.log4j.Logger;
import org.scigap.iucig.service.MWSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/nodeInfo/")
public class NodeController {
	
	@Autowired
	MWSService service;
	
	@Value("${quarry.mws.url}")
	private String QUARRY_MWS_URL;

    @Value("${br2.mws.url}")
    private String BR2_MWS_URL;

    @Value("${mason.mws.url}")
    private String MASON_MWS_URL;
	
	private final Logger logger = Logger.getLogger(getClass());
	
	@ResponseBody
	@RequestMapping(value="/quarry")
	public String getAllNodesQuarry(HttpServletResponse response) {
		HttpRequestBase mwsRequest = new HttpGet(QUARRY_MWS_URL +"nodes?api-version=2");
		logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
		return service.getResponseForRequest(mwsRequest, response);
	}

    @ResponseBody
    @RequestMapping(value="/quarry/nodestatus")
    public String getAllNodeStatus(HttpServletResponse response) {
        HttpRequestBase mwsRequest = new HttpGet(QUARRY_MWS_URL +"nodes?api-version=2&fields=name,states.state");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }

    @ResponseBody
    @RequestMapping(value="/mason" , method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public String getAllNodesMason(HttpServletResponse response) {
        HttpRequestBase mwsRequest = new HttpGet(MASON_MWS_URL +"nodes?api-version=2");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }

    @ResponseBody
    @RequestMapping(value="/mason/nodestatus")
    public String getMasonAllNodeStatus(HttpServletResponse response) {
        HttpRequestBase mwsRequest = new HttpGet(MASON_MWS_URL +"nodes?api-version=2&fields=name,states.state");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }

    @ResponseBody
    @RequestMapping(value="/bigred2")
    public String getAllNodesBR2(HttpServletResponse response) {
        HttpRequestBase mwsRequest = new HttpGet(BR2_MWS_URL +"nodes?api-version=2");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }

    @ResponseBody
    @RequestMapping(value="/bigred2/nodestatus")
    public String getAllNodeStatusBR2(HttpServletResponse response) {
        HttpRequestBase mwsRequest = new HttpGet(BR2_MWS_URL +"nodes?api-version=2&fields=name,states.state");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }
}
