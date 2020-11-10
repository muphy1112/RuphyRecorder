package me.muphy.servicce;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemService {

    @Autowired
    WebApplicationContext applicationContext;

    public List<Map<String, String>> getAllUrl(String parentUrl) {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> methods = mapping.getHandlerMethods();
        List<Map<String, String>> list = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : methods.entrySet()) {
            Map<String, String> map = new HashMap<>();
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            PatternsRequestCondition p = info.getPatternsCondition();
            map.put("className", method.getMethod().getDeclaringClass().getName()); // 类名
            map.put("method", method.getMethod().getName()); // 方法名
            for (String url : p.getPatterns()) {
                map.put("url", url);
            }
            map.put("parameterTypes", JSON.toJSONString(method.getMethod().getParameterTypes()));
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                map.put("type", requestMethod.toString());
            }
            if (!StringUtils.isEmpty(map.get("url")) && (StringUtils.isEmpty(parentUrl) || map.get("url").contains(parentUrl))) {
                list.add(map);
            }
        }
        return list;
    }
}
