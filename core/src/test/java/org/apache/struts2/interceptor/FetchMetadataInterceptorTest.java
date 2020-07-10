package org.apache.struts2.interceptor;


import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.Arrays;
import java.util.HashSet;

// TODO Vary headers tests
// TODO  integration test where we set up a few interceptors and check that the chain gets interrupted when an attack reaches our interceptor

public class FetchMetadataInterceptorTest extends XWorkTestCase {

    private final FetchMetadataInterceptor interceptor = new FetchMetadataInterceptor(new HashSet<>(Arrays.asList("/foo", "/bar")));


    public void testNoSite() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.removeHeader("sec-fetch-site");

        ServletActionContext.setRequest(request);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
    }

    public void testValidSite() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();

        for (String header : Arrays.asList("same-origin", "same-site", "none")){
            request.addHeader("sec-fetch-site", header);

            ServletActionContext.setRequest(request);
            ActionContext context = ServletActionContext.getActionContext();
            mai.setInvocationContext(context);

            assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
            request.removeHeader("sec-fetch-site");
        }

    }

    public void testValidTopLevelNavigation() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader("sec-fetch-mode", "navigate");
        request.addHeader("sec-fetch-dest", "script");
        request.setMethod("GET");

        ServletActionContext.setRequest(request);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
    }

    public void testInValidTopLevelNavigation() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();

        for (String header : Arrays.asList("object", "embed")) {
            request.addHeader("sec-fetch-site", "foo");
            request.addHeader("sec-fetch-mode", "navigate");
            request.addHeader("sec-fetch-dest", header);
            request.setMethod("GET");

            ServletActionContext.setRequest(request);
            ActionContext context = ServletActionContext.getActionContext();
            mai.setInvocationContext(context);


            assertEquals("Expected interceptor to NOT accept this request", "403", interceptor.intercept(mai));
            request.removeHeader("sec-fetch-dest");
        }
    }

    public void testPathInExemptedPaths() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foo");

        ServletActionContext.setRequest(request);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
    }

    public void testPathNotInExemptedPaths() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foobar");

        ServletActionContext.setRequest(request);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        assertEquals("Expected interceptor to NOT accept this request", "403", interceptor.intercept(mai));
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container.inject(interceptor);
    }

}
