# smsTest
springboot+spring+mybatis,dubbo+zk，redis测试例子；

springboot项目打包后部署外部Tomcat出现ErrorPageFilter异常--处理方案
@Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }
 
    @Bean
    public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }
原文：https://blog.csdn.net/weixin_41622183/article/details/84652437 

