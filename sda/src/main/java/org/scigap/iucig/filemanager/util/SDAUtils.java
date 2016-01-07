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

package org.scigap.iucig.filemanager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class SDAUtils {
    private static final Logger log = LoggerFactory.getLogger(SDAUtils.class);
    public static String getPortalURL () throws IOException {
        try {
            Properties properties = new Properties();
            URL resource = SDAUtils.class.getClassLoader().getResource(Constants.KERB_PROPERTIES);
            if (resource != null){
                properties.load(resource.openStream());
                return properties.getProperty(Constants.PORTAL_URL);
            }
        } catch (IOException e) {
            log.error("Unable to read " + Constants.KERB_PROPERTIES, e);
            throw new IOException("Unable to read " + Constants.KERB_PROPERTIES, e);
        }
        return null;
    }
}
