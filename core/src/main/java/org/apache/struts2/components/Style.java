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

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * Add nonce propagation feature to implement CSP in style tags
 * </p>
 *
 * <p>
 * The style tag allows users to either load external resources or inline style sheets. 
 * Several exfiltration techniques exist that leverage CSS injection to steal secret or sensitive information from documents. For this reason, it is recommended to protect against CSS injections with nonce-based CSP. 
 * This s:style tag includes a nonce attribute that is randomly generated with each request. Only style blocks with a valid nonce will be executed if CSP is in use.
 * </p>
 *
 * <p><b>Examples</b></p>
 *
 * <pre>
 *
 * JSP
 *      &lt;s:style ... /&gt;
 *
 * Freemarker
 *      &lt;@s.style .../&gt;
 *
 * </pre>
 *
 */
@StrutsTag(name="style",
        tldTagClass="org.apache.struts2.views.jsp.ui.StyleTag",
        description="Style tag automatically adds nonces to style elements - should be used in combination with Struts' CSP Interceptor.",
        allowDynamicAttributes=true)
public class Style extends ClosingUIBean{

    private static final String TEMPLATE = "style-close";
    private static final String OPEN_TEMPLATE = "style";

    protected String type;
    protected String media;
    protected String title;

    public Style(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @StrutsTagAttribute(description="HTML style type attribute")
    public void setType(String type) {
        this.type = type;
    }

    @StrutsTagAttribute(description="HTML style media attribute")
    public void setMedia(String media) {
        this.media = media;
    }

    @StrutsTagAttribute(description="HTML style title attribute")
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (type != null) {
            addParameter("type", findString(type));
        }

        if (media != null) {
            addParameter("media", findString(media));
        }

        if (title != null) {
            addParameter("title", findString(title));
        }
    }
}
