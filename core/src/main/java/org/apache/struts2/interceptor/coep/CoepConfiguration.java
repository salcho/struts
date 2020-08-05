/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.interceptor.coep;


import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CoepConfiguration {

    private static final String REQUIRE_COEP_HEADER = "require-corp";
    private static final String COEP_ENFORCING_HEADER = "Cross-Origin-Embedder-Policy";
    private static final String COEP_REPORT_HEADER = "Cross-Origin-Embedder-Policy-Report-Only";
    private static final String REPORT_TO_HEADER = "Report-to";

    private String reportUri = "/coep-reports"; // TODO find default coep uri
    private boolean enforcingMode = true;
    private boolean disabled = false;
    private String header = COEP_ENFORCING_HEADER;
    private String path;

    public void addHeader(Map<String, Object> session, HttpServletResponse res){
        if (disabled ){ return; }

        String headerContent = String.format("%s",
                REQUIRE_COEP_HEADER);
        res.setHeader(header, headerContent);
    }

    // TODO need better coding practice than that
//    private String getFullReportUri(){
//        int len = path.length();
//        if (reportUri.length() >= len) {
//            if (reportUri.substring(len).equals(path)) {
//                return reportUri;
//            }
//        }
//        if (reportUri.charAt(0) == '/') {
//            return reportUri = path+reportUri;
//        } else {
//            return reportUri;
//        }
//    }

    public void setPath(String value){
        path = value;
    }

    public void setEnforcingMode(boolean mode){
        this.enforcingMode = mode;
        if (enforcingMode){
            header = COEP_ENFORCING_HEADER;
        } else {
            header = COEP_REPORT_HEADER;
        }
    }

    public void setReportUri(String value){
        reportUri = value;
    }

    public void setDisabled(Boolean value){
        disabled = value;
    }
}
