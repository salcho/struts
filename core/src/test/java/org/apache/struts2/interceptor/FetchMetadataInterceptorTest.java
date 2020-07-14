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
    private final MockActionInvocation mai = new MockActionInvocation();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private static final String VARY_HEADER_VALUE = String.format("%s,%s,%s", DefaultResourceIsolationPolicy.SEC_FETCH_DEST_HEADER, DefaultResourceIsolationPolicy.SEC_FETCH_SITE_HEADER, DefaultResourceIsolationPolicy.SEC_FETCH_MODE_HEADER);

    public void testNoSite() throws Exception {
        request.removeHeader("sec-fetch-site");

        assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
    }

    public void testValidSite() throws Exception {
        for (String header : Arrays.asList("same-origin", "same-site", "none")){
            request.addHeader("sec-fetch-site", header);

            assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
        }

    }

    public void testValidTopLevelNavigation() throws Exception {
        request.addHeader("sec-fetch-mode", "navigate");
        request.addHeader("sec-fetch-dest", "script");
        request.setMethod("GET");

        assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
    }

    public void testInValidTopLevelNavigation() throws Exception {
        for (String header : Arrays.asList("object", "embed")) {
            request.addHeader("sec-fetch-site", "foo");
            request.addHeader("sec-fetch-mode", "navigate");
            request.addHeader("sec-fetch-dest", header);
            request.setMethod("GET");

            assertEquals("Expected interceptor to NOT accept this request", "403", interceptor.intercept(mai));
        }
    }

    public void testPathInExemptedPaths() throws Exception {
        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foo");

        assertFalse("Expected interceptor to accept this request", "403".equals(interceptor.intercept(mai)));
    }

    public void testPathNotInExemptedPaths() throws Exception {
        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foobar");

        assertEquals("Expected interceptor to NOT accept this request", "403", interceptor.intercept(mai));
    }

    public void testVaryHeaderAcceptedReq() throws Exception {
        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foo");

        interceptor.intercept(mai);

        assertTrue("Expected vary header to be included", response.containsHeader(ResourceIsolationPolicy.VARY_HEADER));
        assertEquals("Expected different vary header value", response.getHeader(ResourceIsolationPolicy.VARY_HEADER), VARY_HEADER_VALUE);
    }

    public void testVaryHeaderRejectedReq() throws Exception {
        request.addHeader("sec-fetch-site", "foo");

        interceptor.intercept(mai);

        assertTrue("Expected vary header to be included", response.containsHeader(ResourceIsolationPolicy.VARY_HEADER));
        assertEquals("Expected different vary header value", response.getHeader(ResourceIsolationPolicy.VARY_HEADER), VARY_HEADER_VALUE);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container.inject(interceptor);
        interceptor.setExemptedPaths("/foo,/bar");
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);
    }

}
