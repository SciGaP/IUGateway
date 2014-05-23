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

import javax.servlet.http.HttpServletRequest;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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
        HttpRequestBase disciplines = new HttpGet(SCIENCE_DISCIPLINE_URL + "discipline/?format=json");
        logger.debug("Executing REST GET request" + disciplines.getRequestLine());

        try {
            httpClient = (DefaultHttpClient) WebClientDevWrapper.wrapClient(httpClient);
            HttpResponse response = httpClient.execute(disciplines);
            HttpEntity entity = response.getEntity();
            if (entity != null && response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
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

    @ResponseBody
    @RequestMapping(value = "/getUsersScienceDiscipline", method = RequestMethod.GET)
    public String getUsersScienceDiscipline(HttpServletRequest request) throws Exception {
        String responseJSON = null;
        String remoteUser;
        if (request != null) {
            remoteUser = request.getRemoteUser();
        } else {
            throw new Exception("Remote user is null");
        }
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = SCIENCE_DISCIPLINE_URL + "user/" + remoteUser + "?format=json&fields=disciplines";
        System.out.println(url);
        HttpRequestBase disciplines = new HttpGet(url);
        logger.debug("Executing REST GET request" + disciplines.getRequestLine());

        try {
            httpClient = (DefaultHttpClient) WebClientDevWrapper.wrapClient(httpClient);
            HttpResponse response = httpClient.execute(disciplines);
            HttpEntity entity = response.getEntity();
            if (entity != null && response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
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
    public void updateScienceDiscipline(@RequestBody ScienceDiscipline discipline, HttpServletRequest request) throws Exception {
        try {
            String remoteUser;
            if (request != null) {
                remoteUser = request.getRemoteUser();
            } else {
                throw new Exception("Remote user is null");
            }
            int primarySubDisId = 0;
            int secondarySubDisId = 0;
            int tertiarySubDisId = 0;
            String urlParameters = "user=" + remoteUser;
            if (discipline != null) {
                Map<String, String> primarySubDisc = discipline.getPrimarySubDisc();
                if (primarySubDisc != null && !primarySubDisc.isEmpty()) {
                    for (String key : primarySubDisc.keySet()) {
                        if (key.equals("id")) {
                            primarySubDisId = Integer.valueOf(primarySubDisc.get(key));
                            urlParameters += "&discipline1=" + primarySubDisId;
                        }
                    }
                } else {
                    Map<String, Object> primaryDiscipline = discipline.getPrimaryDisc();
                    if (primaryDiscipline != null && !primaryDiscipline.isEmpty()) {
                        Object subdisciplines = primaryDiscipline.get("subdisciplines");
                        if (subdisciplines instanceof ArrayList) {
                            for (int i = 0; i < ((ArrayList) subdisciplines).size(); i++) {
                                Object disc = ((ArrayList) subdisciplines).get(i);
                                if (disc instanceof HashMap) {
                                    if (((HashMap) disc).get("name").equals("Other / Unspecified")) {
                                        primarySubDisId = Integer.valueOf((((HashMap) disc).get("id")).toString());
                                    }
                                }
                            }
                            urlParameters += "&discipline1=" + primarySubDisId;
                        }
                    }
                }
                Map<String, String> secondarySubDisc = discipline.getSecondarySubDisc();
                if (secondarySubDisc != null && !secondarySubDisc.isEmpty()) {
                    for (String key : secondarySubDisc.keySet()) {
                        if (key.equals("id")) {
                            secondarySubDisId = Integer.valueOf(secondarySubDisc.get(key));
                            urlParameters += "&discipline2=" + secondarySubDisId;
                        }
                    }
                } else {
                    Map<String, Object> secondaryDisc = discipline.getSecondaryDisc();
                    if (secondaryDisc != null && !secondaryDisc.isEmpty()) {
                        Object subdisciplines = secondaryDisc.get("subdisciplines");
                        if (subdisciplines instanceof ArrayList) {
                            for (int i = 0; i < ((ArrayList) subdisciplines).size(); i++) {
                                Object disc = ((ArrayList) subdisciplines).get(i);
                                if (disc instanceof HashMap) {
                                    if (((HashMap) disc).get("name").equals("Other / Unspecified")) {
                                        secondarySubDisId = Integer.valueOf((((HashMap) disc).get("id")).toString());
                                    }
                                }
                            }
                            urlParameters += "&discipline2=" + secondarySubDisId;
                        }
                    }
                }

                Map<String, String> tertiarySubDisc = discipline.getTertiarySubDisc();
                if (tertiarySubDisc != null && !tertiarySubDisc.isEmpty()) {
                    for (String key : tertiarySubDisc.keySet()) {
                        if (key.equals("id")) {
                            tertiarySubDisId = Integer.valueOf(tertiarySubDisc.get(key));
                            urlParameters += "&discipline3=" + tertiarySubDisId;
                        }
                    }
                }else {
                    Map<String, Object> tertiaryDisc = discipline.getTertiaryDisc();
                    if (tertiaryDisc != null && !tertiaryDisc.isEmpty()) {
                        Object subdisciplines = tertiaryDisc.get("subdisciplines");
                        if (subdisciplines instanceof ArrayList) {
                            for (int i = 0; i < ((ArrayList) subdisciplines).size(); i++) {
                                Object disc = ((ArrayList) subdisciplines).get(i);
                                if (disc instanceof HashMap) {
                                    if (((HashMap) disc).get("name").equals("Other / Unspecified")) {
                                        tertiarySubDisId = Integer.valueOf((((HashMap) disc).get("id")).toString());
                                    }
                                }
                            }
                            urlParameters += "&discipline3=" + tertiarySubDisId;
                        }
                    }
                }
                URL obj = new URL(SCIENCE_DISCIPLINE_URL + "discipline/");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                urlParameters += "&date=" + discipline.getDate() + "&source=cybergateway&commit=Update";
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'POST' request to URL : " + SCIENCE_DISCIPLINE_URL);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @ResponseBody
//    @RequestMapping(value = "/updateScienceDiscipline", method = RequestMethod.POST)
//    public void updateScienceDiscipline1(@RequestBody ScienceDiscipline discipline, HttpServletRequest request) throws Exception{
//        try {
//            String remoteUser;
//            if (request != null){
//                remoteUser = request.getRemoteUser();
//            } else {
//                throw new Exception("Remote user is null");
//            }
//            String subDiscId1 = "sub-";
//            String subDiscId2 = "sub-";
//            String subDiscId3 = "sub-";
//            int primaryDisId = 0;
//            int primarySubDisId = 0;
//            int secondaryDisId = 0;
//            int secondarySubDisId = 0;
//            int tertiaryDisId = 0;
//            int tertiarySubDisId = 0;
//            String urlParameters = "user=" + remoteUser;
//            if (discipline != null) {
//                Map<String, Object> primaryDiscipline = discipline.getPrimaryDisc();
//                if (primaryDiscipline != null && !primaryDiscipline.isEmpty()){
//                    for (String key : primaryDiscipline.keySet()) {
//                        if (key.equals("id")) {
//                            primaryDisId = Integer.valueOf(primaryDiscipline.get(key).toString());
//                            urlParameters += "&discipline=" + primaryDisId;
//                        }
//                    }
//                }
//                Map<String, String> primarySubDisc = discipline.getPrimarySubDisc();
//                if (primarySubDisc != null && !primarySubDisc.isEmpty()){
//                    for (String key : primarySubDisc.keySet()) {
//                        if (key.equals("id")) {
//                            primarySubDisId = Integer.valueOf(primarySubDisc.get(key));
//                            subDiscId1 += primaryDisId;
//                            urlParameters += "&" + subDiscId1 + "=" + primarySubDisId;
//                        }
//                    }
//                }
//
//                Map<String, Object> secondaryDisc = discipline.getSecondaryDisc();
//                if (secondaryDisc != null && !secondaryDisc.isEmpty()){
//                    for (String key : secondaryDisc.keySet()) {
//                        if (key.equals("id")) {
//                            secondaryDisId = Integer.valueOf(secondaryDisc.get(key).toString());
//                        }
//                    }
//                }
//
//                Map<String, String> secondarySubDisc = discipline.getSecondarySubDisc();
//                if (secondarySubDisc != null && !secondarySubDisc.isEmpty()){
//                    for (String key : secondarySubDisc.keySet()) {
//                        if (key.equals("id")) {
//                            secondarySubDisId = Integer.valueOf(secondarySubDisc.get(key));
//                            subDiscId2 += secondaryDisId;
//                            urlParameters += "&" + subDiscId2 + "=" + secondarySubDisId;
//                        }
//                    }
//                }
//
//                Map<String, Object> tertiaryDisc = discipline.getTertiaryDisc();
//                if (tertiaryDisc != null && !tertiaryDisc.isEmpty()){
//                    for (String key : tertiaryDisc.keySet()) {
//                        if (key.equals("id")) {
//                            tertiaryDisId = Integer.valueOf(tertiaryDisc.get(key).toString());
//                        }
//                    }
//                }
//
//                Map<String, String> tertiarySubDisc = discipline.getTertiarySubDisc();
//                if (tertiarySubDisc != null && !tertiarySubDisc.isEmpty()){
//                    for (String key : tertiarySubDisc.keySet()) {
//                        if (key.equals("id")) {
//                            tertiarySubDisId = Integer.valueOf(tertiarySubDisc.get(key));
//                            subDiscId3 += tertiaryDisId;
//                            urlParameters += "&" + subDiscId3 + "=" + tertiarySubDisId;
//                        }
//                    }
//                }
//
//                URL obj = new URL(SCIENCE_DISCIPLINE_URL + "discipline/");
//                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//                con.setRequestMethod("POST");
//                con.setDoInput (true);
//                con.setDoOutput (true);
//                con.setUseCaches (false);
//                urlParameters += "&date=" + discipline.getDate() +  "&source=iugateway&commit=Add";
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
//    }

}
