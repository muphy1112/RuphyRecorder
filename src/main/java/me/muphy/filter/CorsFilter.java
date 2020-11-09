package me.muphy.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(filterName = "corsFilter", urlPatterns = "/*")
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletResponse instanceof HttpServletResponse) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            // 添加参数，允许任意domain访问
            response.setHeader("Access-Control-Allow-Origin", "*");
            // 这个allow-headers要配为*，这样才能允许所有的请求头
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST,GET");
            response.setHeader("Access-Control-Max-Age", 20 * 24 * 60 * 60 + "");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
