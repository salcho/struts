package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.logging.log4j.util.Strings;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.interceptor.coep.CoepInterceptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;
import java.util.Map;

public class CoepInterceptorTest extends StrutsInternalTestCase {

    private final CoepInterceptor interceptor = new CoepInterceptor();
    private final MockActionInvocation mai = new MockActionInvocation();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    private final String defaultReportUri = "/coep-reports";
    private final String COEP_ENFORCING_HEADER = "Cross-Origin-Embedder-Policy";
    private final String COEP_REPORT_HEADER = "Cross-Origin-Embedder-Policy-Report-Only";
    private final String HEADER_CONTENT = String.format("require-corp; report-to='%s'", defaultReportUri);


    public void testDisabled() throws Exception {
        interceptor.setDisabled("true");

        interceptor.intercept(mai);

        String header = response.getHeader(COEP_ENFORCING_HEADER);
        assertTrue("COEP is not disabled", Strings.isEmpty(header));
    }

    public void testEnforcingHeader() throws Exception {
        request.setContextPath("/foo");
        interceptor.setEnforcingMode("true");

        interceptor.intercept(mai);

        String header = response.getHeader(COEP_ENFORCING_HEADER);
        assertFalse("COEP enforcing header does not exist", Strings.isEmpty(header));
        assertEquals("COEP header value is incorrect", HEADER_CONTENT, header);
    }

    public void testExemptedPath() throws Exception{
        interceptor.setExemptedPaths("/foo");
        request.setContextPath("/foo");
        interceptor.setEnforcingMode("true");

        interceptor.intercept(mai);

        String header = response.getHeader(COEP_ENFORCING_HEADER);
        assertTrue("COEP applied to exempted path", Strings.isEmpty(header));
    }

    public void testReportingHeader() throws Exception {
        request.setContextPath("/foo");
        interceptor.setEnforcingMode("false");

        interceptor.intercept(mai);

        String header = response.getHeader(COEP_REPORT_HEADER);
        assertFalse("COEP reporting header does not exist", Strings.isEmpty(header));
        assertEquals("COEP header value is incorrect", HEADER_CONTENT, header);
    }

    public void testCannotParseUri() throws Exception {
        interceptor.setEnforcingMode("false");
        try{
            interceptor.setReportUri("ww w. google.@com");
            assert(false);
        } catch (IllegalArgumentException e){
            assert(true);
        }
    }
    public void testCannotParseRelativeUri() throws Exception {
        interceptor.setEnforcingMode("false");
        try{
            interceptor.setReportUri("some-uri");
            assert(false);
        } catch (IllegalArgumentException e){
            assert(true);
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container.inject(interceptor);
//        interceptor.setExemptedPaths("/foo,/bar");
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        Map<String, Object> session = new HashMap<>();
        context.withSession(session);
        mai.setInvocationContext(context);
    }

}
