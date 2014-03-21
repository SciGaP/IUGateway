/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.scigap.iucig.controller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.scigap.iucig.gateway.util.ScienceDiscipline;
import org.scigap.iucig.gateway.util.WebClientDevWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@Controller
public class ScienceDisciplineController {
    private final Logger logger = Logger.getLogger(ScienceDisciplineController.class);
    @Value("${science.disciplines.url}")
    private String SCIENCE_DISCIPLINE_URL;

    @ResponseBody
    @RequestMapping(value = "/getScienceDiscipline", method = RequestMethod.GET)
    public String getScienceDiscipline() {
        String responseJSON = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpRequestBase disciplines = new HttpGet(SCIENCE_DISCIPLINE_URL +"?format=json");
        logger.debug("Executing REST GET request" + disciplines.getRequestLine());

        try {
            httpClient = (DefaultHttpClient) WebClientDevWrapper.wrapClient(httpClient);
            HttpResponse response = httpClient.execute(disciplines);
            HttpEntity entity = response.getEntity();
            if (entity != null && response.getStatusLine().getStatusCode()== HttpStatus.OK.value()) {
                responseJSON = convertStreamToString(entity.getContent());
            }
            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseJSON;
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @ResponseBody
    @RequestMapping(value = "/updateScienceDiscipline", method = RequestMethod.POST)
    public void updateScienceDiscipline(@RequestBody ScienceDiscipline discipline) {
//        try {
//            if (discipline != null) {
//                int primaryDisId = 0;
//                int primarySubDisId = 0;
//                Map<String, String> primaryDisc = discipline.getPrimaryDisc();
//                for (String key : primaryDisc.keySet()) {
//                    if (key.equals("id")) {
//                        primaryDisId = Integer.valueOf(primaryDisc.get(key));
//                        System.out.println("Id1 : " + primaryDisId);
//                    }
//                }
//                Map<String, String> primarySubDisc = discipline.getPrimarySubDisc();
//                for (String key : primarySubDisc.keySet()) {
//                    if (key.equals("id")) {
//                        primarySubDisId = Integer.valueOf(primarySubDisc.get(key));
//                        System.out.println("Id2 : " + primarySubDisId);
//                    }
//                }
//                URL obj = new URL(SCIENCE_DISCIPLINE_URL);
//                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//                con.setRequestMethod("POST");
//                con.setDoInput (true);
//                con.setDoOutput (true);
//                con.setUseCaches (false);
//                String urlParameters = "&user" + discipline.getUsername() + "&discipline:" + primaryDisId + "&sub-13:" + primarySubDisId
//                        + "&source=rtstats&commit=Add";
//                System.out.println("URL params : " + urlParameters);
//                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//                wr.writeBytes(urlParameters);
//                wr.flush();
//                wr.close();
//                int responseCode = con.getResponseCode();
//                System.out.println("\nSending 'POST' request to URL : " + SCIENCE_DISCIPLINE_URL);
//                System.out.println("Post parameters : " + urlParameters);
//                System.out.println("Response Code : " + responseCode);
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
