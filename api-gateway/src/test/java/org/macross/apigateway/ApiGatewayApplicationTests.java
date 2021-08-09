package org.macross.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;

import java.util.List;

@SpringBootTest
class ApiGatewayApplicationTests {

    @Autowired
    private  RouteLocator routeLocator;

    @Test
    void contextLoads() {
        List<Route> routes = routeLocator.getRoutes();
        System.out.println(routes);
    }

}
