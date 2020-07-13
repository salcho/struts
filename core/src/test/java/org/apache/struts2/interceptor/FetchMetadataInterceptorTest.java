package org.apache.struts2.interceptor;


import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;

public class FetchMetadataInterceptorTest extends XWorkTestCase {

    private final FetchMetadataInterceptor interceptor = new FetchMetadataInterceptor();


    public void testNoSite() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.removeHeader("sec-fetch-site");

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
    }

    public void testValidSite() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        for (String header : Arrays.asList("same-origin", "same-site", "none")){
            request.addHeader("sec-fetch-site", header);

            ServletActionContext.setRequest(request);
            ServletActionContext.setResponse(response);
            ActionContext context = ServletActionContext.getActionContext();
            mai.setInvocationContext(context);

            assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
            request.removeHeader("sec-fetch-site");
        }

    }

    public void testValidTopLevelNavigation() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("sec-fetch-mode", "navigate");
        request.addHeader("sec-fetch-dest", "script");
        request.setMethod("GET");

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
    }

    public void testInValidTopLevelNavigation() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        for (String header : Arrays.asList("object", "embed")) {
            request.addHeader("sec-fetch-site", "foo");
            request.addHeader("sec-fetch-mode", "navigate");
            request.addHeader("sec-fetch-dest", header);
            request.setMethod("GET");

            ServletActionContext.setRequest(request);
            ServletActionContext.setResponse(response);
            ActionContext context = ServletActionContext.getActionContext();
            mai.setInvocationContext(context);


            assertEquals("Expected interceptor to NOT accept this request", "403", interceptor.intercept(mai));
            request.removeHeader("sec-fetch-dest");
        }
    }

    public void testPathInExemptedPaths() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foo");

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
    }

    public void testPathNotInExemptedPaths() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foobar");

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        assertEquals("Expected interceptor to NOT accept this request", "403", interceptor.intercept(mai));
    }

    public void testVaryHeaderAcceptedReq() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foo");

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        interceptor.intercept(mai);

        assertTrue("Expected vary header to be included", response.containsHeader(ResourceIsolationPolicy.VARY_HEADER));
        assertEquals("Expected different vary header value", response.getHeader(ResourceIsolationPolicy.VARY_HEADER), FetchMetadataInterceptor.VARY_HEADER_VALUE);
    }

    public void testVaryHeaderRejectedReq() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("sec-fetch-site", "foo");

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        interceptor.intercept(mai);

        assertTrue("Expected vary header to be included", response.containsHeader(ResourceIsolationPolicy.VARY_HEADER));
        assertEquals("Expected different vary header value", response.getHeader(ResourceIsolationPolicy.VARY_HEADER), FetchMetadataInterceptor.VARY_HEADER_VALUE);
    }

    public void testIntegrationValidRequest() throws Exception{
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieInterceptor cookieInterecptor = new CookieInterceptor();

        request.removeHeader("sec-fetch-site");

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        cookieInterecptor.intercept(mai);
        interceptor.intercept(mai);

        assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
    }

    public void testIntegrationValidRequestDiffOrder() throws Exception{
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieInterceptor cookieInterecptor = new CookieInterceptor();

        request.removeHeader("sec-fetch-site");

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        interceptor.intercept(mai);
        cookieInterecptor.intercept(mai);

        assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
    }

    public void testIntegrationRejectedRequest() throws Exception{
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieInterceptor cookieInterecptor = new CookieInterceptor();

        request.addHeader("sec-fetch-site", "foo");

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        cookieInterecptor.intercept(mai);
        interceptor.intercept(mai);

        assertEquals("Expected interceptor to NOT accept this request", "403", interceptor.intercept(mai));
    }

    public void testIntegrationRejectedRequestDiffOrder() throws Exception{
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieInterceptor cookieInterecptor = new CookieInterceptor();

        request.addHeader("sec-fetch-site", "foo");

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        interceptor.intercept(mai);
        cookieInterecptor.intercept(mai);

        assertEquals("Expected interceptor to NOT accept this request", "403", interceptor.intercept(mai));
    }

    public void testIntegrationMultipleValidRequest() throws Exception{
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieInterceptor cookieInterecptor = new CookieInterceptor();
        NoOpInterceptor noOpInterceptor = new NoOpInterceptor();

        request.addHeader("sec-fetch-site", "foo");

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        cookieInterecptor.intercept(mai);
        interceptor.intercept(mai);
        noOpInterceptor.intercept(mai);

        assertEquals("Expected interceptor to NOT accept this request", "403", interceptor.intercept(mai));
    }

    public void testIntegrationMultipleRejectedRequest() throws Exception{
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieInterceptor cookieInterecptor = new CookieInterceptor();
        NoOpInterceptor noOpInterceptor = new NoOpInterceptor();

        request.addHeader("sec-fetch-site", "foo");

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        cookieInterecptor.intercept(mai);
        interceptor.intercept(mai);
        noOpInterceptor.intercept(mai);

        assertEquals("Expected interceptor to NOT accept this request", "403", interceptor.intercept(mai));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container.inject(interceptor);
        interceptor.setExemptedPaths("/foo,/bar");
    }

}
