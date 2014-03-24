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

package org.scigap.iucig.gateway.util;

import java.io.Serializable;
import java.util.Map;

public class ScienceDiscipline implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Object> primaryDisc;
    private Map<String, Object> secondaryDisc;
    private Map<String, Object> tertiaryDisc;
    private String username;
    private String date;
    private Map<String, String> primarySubDisc;
    private Map<String, String> secondarySubDisc;
    private Map<String, String> tertiarySubDisc;

    public Map<String, Object> getPrimaryDisc() {
        return primaryDisc;
    }

    public void setPrimaryDisc(Map<String, Object> primaryDisc) {
        this.primaryDisc = primaryDisc;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, String> getPrimarySubDisc() {
        return primarySubDisc;
    }

    public void setPrimarySubDisc(Map<String, String> primarySubDisc) {
        this.primarySubDisc = primarySubDisc;
    }

    public Map<String, Object> getSecondaryDisc() {
        return secondaryDisc;
    }

    public void setSecondaryDisc(Map<String, Object> secondaryDisc) {
        this.secondaryDisc = secondaryDisc;
    }

    public Map<String, Object> getTertiaryDisc() {
        return tertiaryDisc;
    }

    public void setTertiaryDisc(Map<String, Object> tertiaryDisc) {
        this.tertiaryDisc = tertiaryDisc;
    }

    public Map<String, String> getSecondarySubDisc() {
        return secondarySubDisc;
    }

    public void setSecondarySubDisc(Map<String, String> secondarySubDisc) {
        this.secondarySubDisc = secondarySubDisc;
    }

    public Map<String, String> getTertiarySubDisc() {
        return tertiarySubDisc;
    }

    public void setTertiarySubDisc(Map<String, String> tertiarySubDisc) {
        this.tertiarySubDisc = tertiarySubDisc;
    }
}


