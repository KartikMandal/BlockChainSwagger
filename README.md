# BlockChainSwagger
# BlockChainSwagger

Step 1>Add in pom.xml
		<dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.4.2</version>
        </dependency>
		<dependency>
		    <groupId>io.springfox</groupId>
		    <artifactId>springfox-swagger2</artifactId>
		    <version>2.8.0</version>
		    <scope>compile</scope>
		</dependency>
	    <dependency>
		    <groupId>io.springfox</groupId>
		    <artifactId>springfox-swagger-ui</artifactId>
		    <version>2.8.0</version>
		    <scope>compile</scope>
		</dependency>

		
Step 2> Add Application-context.xml or Dispatcher-Servlet.xml



<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/mvc"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:beans="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
	<mvc:default-servlet-handler/>
	<beans:bean id="swagger2Config" class="springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration"/>
	<mvc:resources location="classpath:/META-INF/resources/" mapping="swagger-ui.html"/>
	<mvc:resources location="classpath:/META-INF/resources/webjars/" mapping="/webjars/**"/>
	
	<!-- DispatcherServlet Context: defines this servlet's request-processing infrastructure -->
	
	<!-- Enables the Spring MVC @Controller programming model -->
	<annotation-driven />

	<!-- Handles HTTP GET requests for /resources/** by efficiently serving up static resources in the ${webappRoot}/resources directory -->
	<resources mapping="/resources/**" location="/resources/" />

	<!-- Resolves views selected for rendering by @Controllers to .jsp resources in the /WEB-INF/views directory -->
	<beans:bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<beans:property name="prefix" value="/WEB-INF/views/" />
		<beans:property name="suffix" value=".jsp" />
	</beans:bean>
	
	<!-- Configure to plugin JSON as request and response in method handler -->
	<beans:bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
		<beans:property name="messageConverters">
			<beans:list>
				<beans:ref bean="jsonMessageConverter"/>
			</beans:list>
		</beans:property>
	</beans:bean>
	
	
	<!-- Configure bean to convert JSON to POJO and vice versa -->
	<beans:bean id="jsonMessageConverter" class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
	</beans:bean>	
	
	<context:component-scan base-package="com.yodlee.garage.fest" />
	
</beans:beans>


Step 3> Add in all controller class 
@Api(value="blockChain", description="blockchain information")