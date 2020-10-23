package me.muphy.handler;

import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperBeanPostProcessor implements BeanPostProcessor {

    private static Logger logger = LoggerFactory.getLogger(MapperBeanPostProcessor.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        if (interfaces == null || interfaces.length <= 0) {
            return bean;
        }
        for (Class<?> clazz : interfaces) {
            if (clazz.isAnnotationPresent(Mapper.class)) {
                Object instance = new PagingInvocationHandler().getInstance(bean);
                logger.info("为" + beanName + "创建代理成功！");
                return instance;
            }
        }
        return bean;
    }
}
