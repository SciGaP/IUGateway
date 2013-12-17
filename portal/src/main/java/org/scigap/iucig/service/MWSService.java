/**
 * 
 */
package org.scigap.iucig.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.scigap.iucig.gateway.util.WebClientDevWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * @author Viknes
 *
 */
@Service(value="communicationService")
public class MWSService {
	
	@Value("${mws.username}")
	private String mwsUsername;
	
	@Value("${mws.password}")
	private String mwsPassword;

	private final Logger logger = Logger.getLogger(getClass());

	public String getResponseForRequest(HttpRequestBase request, HttpServletResponse servletResponse) {
		String responseJSON = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Base64 encoder = new Base64();
		
		try {
			// Begin - Only for DEV environment to avoid certificate exception
			httpClient = (DefaultHttpClient) WebClientDevWrapper.wrapClient(httpClient);
			// End
			String userCredentails = new String(encoder.encode((mwsUsername+":"+mwsPassword).getBytes()));
			request.setHeader("Authorization", "Basic " + userCredentails);
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			logger.debug(response.getStatusLine());
			if (entity != null && response.getStatusLine().getStatusCode()==HttpStatus.OK.value()) {
				responseJSON = convertStreamToString(entity.getContent());
			}
			EntityUtils.consume(entity);
		} catch (IOException e) {
			logger.error(e);			
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		if(responseJSON==null) {
			servletResponse.setStatus(HttpStatus.BAD_GATEWAY.value());
            responseJSON = "Invalid response from MWS";
		}
		return responseJSON;
	}	

	private String convertStreamToString(InputStream is) {
	    Scanner s = new Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
}
