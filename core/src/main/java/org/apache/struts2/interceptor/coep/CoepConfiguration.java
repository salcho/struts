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


import com.opensymphony.xwork2.util.TextParseUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoepConfiguration {

    private final String REQUIRE_COEP_HEADER = "require-corp";
    private final String COEP_ENFORCING_HEADER = "Cross-Origin-Embedder-Policy";
    private final String COEP_REPORT_HEADER = "Cross-Origin-Embedder-Policy-Report-Only";
    private final String REPORT_TO_HEADER = "Report-to";

    private String reportUri = "/coep-reports"; // TODO find default coep uri
    private boolean enforcingMode = true;
    private boolean disabled = false;
    private String header = COEP_ENFORCING_HEADER;

    public void addHeader(Map<String, Object> session, HttpServletResponse res){
        if (disabled ){ return; }

        String headerContent = String.format("%s; report-to='%s'",
                REQUIRE_COEP_HEADER,
                reportUri);
        res.addHeader(header, headerContent);

        if (session.containsKey(REPORT_TO_HEADER)){
            throw new IllegalArgumentException("Session already contains a report-to header");
            // TODO change the exception
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("group", reportUri);
            params.put("max_age", "86400");
            params.put("endpoints", String.format("[{url: '%s'}]", reportUri));
            res.addHeader(REPORT_TO_HEADER, params.toString());
        }
    }

//    Report-To: { group: 'coep_rollout_1', max_age: 86400,
//   endpoints: [{ url: 'https://first-party-test.glitch.me/report'}]}
//    Cross-Origin-Embedder-Policy: require-corp; report-to="coep_rollout_1"

    private String getFullReportUri(){
        // TODO change to full path
        return "dummy";
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
