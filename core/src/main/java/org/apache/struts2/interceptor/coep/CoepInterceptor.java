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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.TextParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class CoepInterceptor extends AbstractInterceptor implements PreResultListener {

    private static final Logger LOG = LoggerFactory.getLogger(CoepInterceptor.class);

    private CoepConfiguration config = new CoepConfiguration();
    private Set<String> exemptedPaths = new HashSet<>();

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        invocation.addPreResultListener(this);
        return invocation.invoke();
    }

    @Override
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        HttpServletRequest req = invocation.getInvocationContext().getServletRequest();
        HttpServletResponse res = invocation.getInvocationContext().getServletResponse();
        final String path = req.getContextPath();

        if (!exemptedPaths.contains(path)){
            Map<String, Object> ses = invocation.getInvocationContext().getSession();
            config.setPath(req.getContextPath());
            config.addHeader(ses, res);
        }
    }

    public void setExemptedPaths(String paths){
        this.exemptedPaths.addAll(TextParseUtil.commaDelimitedStringToSet(paths));
    }

    public void setReportUri(String reportUri) {
        Optional<URI> uri = buildUri(reportUri);
        if (!uri.isPresent()) {
            throw new IllegalArgumentException("Could not parse configured report URI for CSP interceptor: " + reportUri);
        }

        if (!uri.get().isAbsolute() && !reportUri.startsWith("/")) {
            throw new IllegalArgumentException("Illegal configuration: report URI is not relative to the root. Please set a report URI that starts with /");
        }

        config.setReportUri(reportUri);
    }

    private static Optional<URI> buildUri(String reportUri) {
        try {
            return Optional.of(URI.create(reportUri));
        } catch (IllegalArgumentException ignored) {
        }

        return Optional.empty();
    }

    public void setEnforcingMode(String value){
        boolean enforcingMode = Boolean.parseBoolean(value);
        config.setEnforcingMode(enforcingMode);
    }

    public void setDisabled(String value){
        boolean disabled = Boolean.parseBoolean(value);
        config.setDisabled(disabled);
    }
}
