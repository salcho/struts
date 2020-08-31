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
package org.apache.struts2.views.java.simple;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.components.Style;
import org.apache.struts2.components.UIBean;


import java.util.HashMap;
import java.util.Map;


public class StyleTest extends AbstractTest {

    private Style tag;

    private static final String NONCE_VAL = "r4andom";

    public void testRenderScriptTag() {
        tag.setType("text/javascript");
        tag.setMedia("foo");
        tag.setTitle("bar");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();

        assertTrue("Style doesn't have nonce attribute", output.contains("nonce="));
        assertTrue("Style doesn't have type attribute", output.contains("type="));
        assertTrue("Style doesn't have media attribute", output.contains("media="));
        assertTrue("Style doesn't have title attribute", output.contains("title"));
    }

    @Override
    protected UIBean getUIBean() throws Exception {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "style";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ActionContext actionContext = stack.getActionContext();
        Map<String, Object> session = new HashMap<>();
        session.put("nonce", NONCE_VAL);
        actionContext.withSession(session);

        this.tag = new Style(stack, request, response);
    }
}