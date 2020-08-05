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
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.TextParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

public class CoopInterceptor extends AbstractInterceptor implements PreResultListener {

    private static final Logger LOG = LoggerFactory.getLogger(CoopInterceptor.class);
    private static final String SAME_ORIGIN = "same-origin";
    private static final String SAME_ORIGIN_ALLOW_POPUPS = "same-origin-allow-popups";
    private static final String UNSAFE_NONE = "unsafe-none";
    private static final String COOP_HEADER = "Cross-Origin-Opener-Policy";

    private final Set<String> exemptedPaths = new HashSet<>();
    private String mode = SAME_ORIGIN;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        invocation.addPreResultListener(this);
        return invocation.invoke();
    }

    @Override
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        String path = request.getContextPath();

        if (isExempted(path)){
            // no need to add headers
            LOG.debug(String.format("Skipping COOP header for exempted path %s", path));
        } else {
            response.setHeader(COOP_HEADER, getMode());
        }
    }

    public boolean isExempted(String path){
        return exemptedPaths.contains(path);
    }

    public void setExemptedPaths(String paths){
        exemptedPaths.addAll(TextParseUtil.commaDelimitedStringToSet(paths));
    }

    private String getMode(){ return mode; }

    public void setMode(String mode) {
        if (!(mode.equals(SAME_ORIGIN) || mode.equals(SAME_ORIGIN_ALLOW_POPUPS) || mode.equals(UNSAFE_NONE))){
            throw new IllegalArgumentException(String.format("Mode '%s' not recognized!", mode));
        }
        this.mode = mode;
    }
}