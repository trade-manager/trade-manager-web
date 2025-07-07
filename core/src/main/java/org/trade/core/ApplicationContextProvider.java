package org.trade.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static final Logger _log = LoggerFactory.getLogger(ApplicationContextProvider.class);
    private static ApplicationContext applicationContext;

    /**
     * This method is called from within the ApplicationContext once it is
     * done starting up, it will stick a reference to itself into this bean.
     *
     * @param applicationContext a reference to the ApplicationContext.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        ApplicationContextProvider.applicationContext = applicationContext;
    }

    /**
     * This is about the same as applicationContext.getApplicationContext(), except it has its
     * own static handle to the Spring context, so calling this method statically
     * will give access to the Spring application context.
     *
     * @return applicationContext an Object reference to the named bean.
     */
    public static ApplicationContext getApplicationContext() {

        return applicationContext;
    }

    /**
     * This is about the same as context.getBean("beanName"), except it has its
     * own static handle to the Spring context, so calling this method statically
     * will give access to the beans by name in the Spring application context.
     * As in the context.getBean("beanName") call, the caller must cast to the
     * appropriate target class. If the bean does not exist, then a Runtime error
     * will be thrown.
     *
     * @param beanName the name of the bean to get.
     * @return an Object reference to the named bean.
     */
    public static <T> T getBean(Class<T> beanName) {

        return applicationContext.getBean(beanName);
    }

    /**
     * This is about the same as context.getBean("beanName"), except it has its
     * own static handle to the Spring context, so calling this method statically
     * will give access to the beans by name in the Spring application context.
     * As in the context.getBean("beanName") call, the caller must cast to the
     * appropriate target class. If the bean does not exist, then a Runtime error
     * will be thrown.
     *
     * @param beanName the name of the bean to get.
     * @return an Object reference to the named bean.
     */
    public static Object getBean(String beanName) {

        return applicationContext.getBean(beanName);
    }

    /**
     * This is about the same as context.getBean("beanName"), except it has its
     * own static handle to the Spring context, so calling this method statically
     * will give access to the beans by name in the Spring application context.
     * As in the context.getBean("beanName") call, the caller must cast to the
     * appropriate target class. If the bean does not exist, then a Runtime error
     * will be thrown.
     *
     * @param beanName the name of the bean to get.
     * @param args     the args of the bean constructor.
     * @return an Object reference to the named bean.
     */
    public Object getBean(String beanName, Object... args) {

        return applicationContext.getBean(beanName, args);
    }

    /**
     * This is about the same as context.getBean("beanName"), except it has its
     * own static handle to the Spring context, so calling this method statically
     * will give access to the beans by name in the Spring application context.
     * As in the context.getBean("beanName") call, the caller must cast to the
     * appropriate target class. If the bean does not exist, then a Runtime error
     * will be thrown.
     *
     * @param beanName the name of the bean to get.
     * @param args     the args of the bean constructor.
     * @return an Object reference to the named bean.
     */
    public <T> T getBean(Class<T> beanName, Object... args) {

        return applicationContext.getBean(beanName, args);
    }
}

