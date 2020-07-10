package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * <!-- START SNIPPET: description -->
 *
 *
 * Interceptor that implements Fetch Metadata policy on incoming requests used to protect against
 * CSRF, XSSI, and cross-origin information leaks. Uses {@link DefaultResourceIsolationPolicy} to
 * filter the requests allowed to be processed.
 *
 * @see <a href="https://web.dev/fetch-metadata/">https://web.dev/fetch-metadata/</a>
 *
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->

 * <ul>
 *     <li>exemptedPaths - Set of opt out endpoints that are meant to serve
 *              cross-site traffic</li>
 *     <li>resourceIsolationPolicy -Instance of {@link DefaultResourceIsolationPolicy} implementing
 *              the logic for the requests filtering</li>
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 *
 *  No extensions
 *
 * <!-- END SNIPPET: extending -->
 *
 * <!-- START SNIPPET: example -->
 *
 * &lt;action ... &gt;
 *   &lt;interceptor-ref name="defaultStack"/&gt;
 *   &lt;interceptor-ref name="fetchMetadata"&gt;
 *      &lt;parameter TODO add parameter for exempted paths /&gt;
 *   &lt;interceptor-ref/&gt;
 *   ...
 * &lt;/action&gt;
 *
 * <!-- END SNIPPET: example -->
 **/

public class FetchMetadataInterceptor extends AbstractInterceptor implements PreResultListener {

    private final Set<String> exemptedPaths = new HashSet<String>();
    private final ResourceIsolationPolicy resourceIsolationPolicy = new DefaultResourceIsolationPolicy();

    public FetchMetadataInterceptor(Set<String> exemptedPaths){
        this.exemptedPaths.addAll(exemptedPaths);
    }

    @Override
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        // Add Vary headers
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        response.setHeader(resourceIsolationPolicy.VARY_HEADER, resourceIsolationPolicy.SEC_FETCH_DEST_HEADER + ", "
                + resourceIsolationPolicy.SEC_FETCH_SITE_HEADER + ", " + resourceIsolationPolicy.SEC_FETCH_MODE_HEADER);
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext context = invocation.getInvocationContext();
        HttpServletRequest request = context.getServletRequest();

        // Apply exemptions: paths/endpoints meant to be served cross-origin
        if (!(resourceIsolationPolicy.isRequestAllowed(request) || this.exemptedPaths.contains(request.getContextPath()))) {
            return String.valueOf(HttpServletResponse.SC_FORBIDDEN);
        }

        // Adds listener that operates between interceptor and result rendering to set Vary headers
        invocation.addPreResultListener(this);

        return invocation.invoke();
    }
}
