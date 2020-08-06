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
import java.util.Map;
import java.util.Set;


/**
 * <!-- START SNIPPET: description -->
 *
 *
 * Interceptor that implements Cross-Origin Embedder Policy on incoming requests used to protect a
 * document from loading any non-same-origin resources which don't explicitly grant the document
 * permission to be loaded.
 *
 * @see <a href="https://web.dev/why-coop-coep/#coep">https://web.dev/why-coop-coep/#coep</a>
 * @see <a href="https://web.dev/coop-coep/">https://web.dev/coop-coep/</a>
 *
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 *     <li>exemptedPaths - Set of opt out endpoints that are meant to serve
 *              cross-site traffic</li>
 *      <li>enforcingMode - Boolean variable the user can set so that COEP operates in enforcing or
 *              report-only mode (blocking resource and reporting violation or only reporting violation</li>
 *      <li>disabled - Boolean variable the user can set to fully disable COEP</li>
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 *
 *  <ul>
 *      <li>AbstractInterceptor - Fundamental interfce for each interceptor to have access to init(),
 *              destroy() and intercept() methods.</li>
 *      <li>PreResultListener - Allows an interecptor to get access to the reponse object. This way
 *              we canmanipulate the response, for example add headers to the reponse</li>
 *  </ul>
 *
 * <!-- END SNIPPET: extending -->
 *
 * <!-- START SNIPPET: example -->
 *
 * &lt;action ... &gt;
 *   &lt;interceptor-ref name="defaultStack"/&gt;
 *   &lt;interceptor-ref name="coepInterceptor"&gt;
 *      &lt;param name="exemptedPaths"&gt; path1,path2,path3 &lt;para/&gt;
 *      &lt;param name="mode"&gt; true &lt;para/&gt;
 *      &lt;param name="disabled"&gt; false &lt;para/&gt;
 *   &lt;interceptor-ref/&gt;
 *   ...
 * &lt;/action&gt;
 *
 * <!-- END SNIPPET: example -->
 **/
public class CoepInterceptor extends AbstractInterceptor implements PreResultListener {

    private static final Logger LOG = LoggerFactory.getLogger(CoepInterceptor.class);
    private static final String REQUIRE_COEP_HEADER = "require-corp";
    private static final String COEP_ENFORCING_HEADER = "Cross-Origin-Embedder-Policy";
    private static final String COEP_REPORT_HEADER = "Cross-Origin-Embedder-Policy-Report-Only";

    private final Set<String> exemptedPaths = new HashSet<>();
    private boolean enforcingMode = true;
    private boolean disabled = false;
    private String header = COEP_ENFORCING_HEADER;

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
            addHeader(res);
        }
    }

    public void addHeader(HttpServletResponse res){
        if (disabled ){ return; }
        res.setHeader(header, REQUIRE_COEP_HEADER);
    }

    public void setExemptedPaths(String paths){
        this.exemptedPaths.addAll(TextParseUtil.commaDelimitedStringToSet(paths));
    }

    public void setEnforcingMode(String mode){
        this.enforcingMode = Boolean.parseBoolean(mode);;
        if (enforcingMode){
            header = COEP_ENFORCING_HEADER;
        } else {
            header = COEP_REPORT_HEADER;
        }
    }

    public void setDisabled(String value){
        disabled = Boolean.parseBoolean(value);
    }
}
