package me.muphy.handler;

import me.muphy.annotation.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public class PagingInvocationHandler implements InvocationHandler {

    private Object target;

    public Object getInstance(Object target) {
        this.target = target;
        Class<?> clazz = target.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Pageable pageable = method.getAnnotation(Pageable.class);
        if(args != null){
            if (pageable != null && args.length >= 2 && (args[0] instanceof Integer && args[1] instanceof Integer)) {
                String pageParameterName = pageable.page();
                String sizeParameterName = pageable.size();
                RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                String pageParameter = getIntDefaultValue(request.getParameter(pageParameterName), "0");
                String sizeParameter = getIntDefaultValue(request.getParameter(sizeParameterName), "0");
                int pageIndex = getIntDefaultValue(Integer.valueOf(pageParameter), 1) - 1;
                int pageSize = getIntDefaultValue(Integer.valueOf(sizeParameter), 10);
                args[0] = getIntDefaultValue((int)args[0], pageIndex * pageSize);
                args[1] = getIntDefaultValue((int)args[1], pageSize);
            }
            for (int i = 0; i < args.length; i++) {
                if(args[i] instanceof String){
                    args[i] = getIntDefaultValue(Objects.toString(args[i]), "");
                }
            }
        }
        Object o = method.invoke(target, args);
        return o;
    }

    private static int getIntDefaultValue(Integer value, int defaultValue) {
        return value == null || value == 0 ? defaultValue : value;
    }

    private static String getIntDefaultValue(String value, String defaultValue) {
        return StringUtils.isEmpty(value) || "null".equals(value) ? defaultValue : value;
    }
}
