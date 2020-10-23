package me.muphy.filter;

import com.alibaba.fastjson.JSON;
import me.muphy.entity.ClientRequestEntity;
import me.muphy.mapper.RemoteHostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@WebFilter(value = "requestFilter", urlPatterns = "/*")
public class RequestFilter implements Filter {

    @Autowired
    private RemoteHostMapper remoteHostMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            // /p/这个目录是专门记录信息的，不需要报错
            if (!request.getRequestURI().matches("^/p/.+")) {
                saveClientRequestInfo(request);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private boolean saveClientRequestInfo(HttpServletRequest request) {
        ClientRequestEntity requestInfo = new ClientRequestEntity();
        requestInfo.setUrl(request.getRequestURL().toString());
        requestInfo.setCookie(JSON.toJSONString(request.getCookies()));
        requestInfo.setServerIpAddr(request.getLocalAddr());
        requestInfo.setClientIpAddr(request.getRemoteAddr());
        requestInfo.setParameters(JSON.toJSONString(request.getParameterMap()));
        requestInfo.setQueryString(request.getQueryString());
        requestInfo.setUser(request.getRemoteUser());
        requestInfo.setReferrer(request.getHeader("referrer"));
        requestInfo.setMethod(request.getMethod());
        return remoteHostMapper.SaveLocalFilterInfo(requestInfo) > 0;
    }
}
