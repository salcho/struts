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
package org.apache.struts2.views.jsp.ui;


import org.apache.struts2.views.jsp.AbstractUITagTest;

import javax.servlet.jsp.JspException;

public class StyleTagTest extends AbstractUITagTest {

    private static final String NONCE_VAL = "r4andom";

    public void testStyleTagAttributes() {
        StyleTag tag = new StyleTag();


        tag.setType("module");
        tag.setMedia("foo");
        tag.setTitle("bar");

        doStyleTest(tag);
        String s = writer.toString();

        assertTrue("Incorrect title attribute for style tag", s.contains("title=\"bar\""));
        assertTrue("Non-existent media attribute for style tag", s.contains("media=\"foo\""));
        assertTrue("Incorrect type attribute for style tag", s.contains("type=\"module\""));
        assertTrue("Incorrect nonce attribute for style tag", s.contains("nonce=\"" + NONCE_VAL+"\""));
    }

    private void doStyleTest(StyleTag tag) {
        //creating nonce value like the CspInterceptor does
        stack.getActionContext().getSession().put("nonce", NONCE_VAL);
        tag.setPageContext(pageContext);

        try {
            tag.doStartTag();
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

    }
}