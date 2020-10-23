package me.muphy.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebFilter(filterName = "securityFilter", urlPatterns = "/*")
@Component
public class SecurityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            filterChain.doFilter(new SecurityHttpServletRequestWrapper(request), servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 此六行正则表达式 史上最强防御xss攻击、sql注入等，却较少影响用户使用特殊字符和关键字
     */
    public class SecurityHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private Map<String, String> regRep = new HashMap<>();

        public SecurityHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            regRep.put("'([^']+)'", "‘$1’");
            regRep.put("`([^`]+)`", "~$1~");
            regRep.put("'(.*--+)", "‘$1");
            regRep.put("\\\"([^\\\"]+)\\\"", "“$1”");
            regRep.put("\\(([^\\)]+)\\)", "（$1）");
            regRep.put("<([^>]+)>", "&lt;$1&gt;");
        }

        private String replace(String val) {
            if (val != null) {
                for (String key : regRep.keySet()) {
                    val = val.replaceAll(key, regRep.get(key));
                }
            }
            return val;
        }

        @Override
        public String getHeader(String name) {
            return replace(super.getHeader(name));
        }

        @Override
        public Cookie[] getCookies() {
            Cookie[] cookies = super.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    cookies[i].setValue(replace(cookies[i].getValue()));
                }
            }
            return cookies;
        }

        @Override
        public String getQueryString() {
            return replace(super.getQueryString());
        }

        @Override
        public String getParameter(String name) {
            return replace(super.getParameter(name));
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    values[i] = replace(values[i]);
                }
            }
            return values;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> parameterMap = super.getParameterMap();
            if (parameterMap != null) {
                for (String key : parameterMap.keySet()) {
                    String[] values = parameterMap.get(key);
                    if (values != null) {
                        for (int i = 0; i < values.length; i++) {
                            values[i] = replace(values[i]);
                        }
                    }
                }
            }
            return parameterMap;
        }
    }
}
