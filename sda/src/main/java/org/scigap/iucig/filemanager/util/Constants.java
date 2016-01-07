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

public class Constants {

    public static final String KERB_PROPERTIES = "kerb.properties";
    public static final String PORTAL_URL = "portal.url";

    public class ErrorMessages {
        public static final String AUTH_ERROR = "Error occurred while creating SFTP session. This happens due to authentication failure. " +
                "Please check whether you have a valid ticket.. To create a new ticket, clear your browser cache  and reload the base URL...";
    }
}
