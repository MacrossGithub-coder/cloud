package org.macross.apigateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.macross.apigateway.utils.JWTUtils;
import org.macross.apigateway.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Component
@Slf4j
public class LoginFilter extends ZuulFilter {

    @Autowired
    @Qualifier("redisTemplateSlave1")
    private RedisTemplate redisTemplateSlave1;

    private List<String> filterPaths;

    private List<String> unfilterPaths;

    public LoginFilter() {
        super();
        filterPaths = new ArrayList<>();
        unfilterPaths = new ArrayList<>();
        filterPaths.add("/apigateway/*/api/v1/pri/**");
        unfilterPaths.add("/apigateway/*/api/v1/pri/user/login");
        unfilterPaths.add("/apigateway/*/api/v1/pri/user/register");
    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    /**
     * 过滤器顺序，越小越先执行
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            log.info("OPTIONS请求不做拦截操作");
            //为OPTIONS请求返回状态码200并阻止转发到下游服务
            requestContext.setResponseStatusCode(200);
            requestContext.setSendZuulResponse(false);
            return false;
        }
        PathMatcher matcher = new AntPathMatcher();
        String uri = request.getRequestURI();
        Optional<String> optional = unfilterPaths.stream().filter(t -> matcher.match(t, uri)).findFirst();
        if (optional.isPresent()) return false;
        optional = filterPaths.stream().filter(t -> matcher.match(t, uri)).findFirst();
        log.info("filter private uri =  [{}] ", uri);
        return optional.isPresent();
    }

    @Override
    public Object run(){

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = requestContext.getResponse();

        try {

            String accessToken = request.getHeader("token");
            if (accessToken == null) {
                accessToken = request.getParameter("token");
            }
            if (StringUtils.isNotBlank(accessToken)) {

                //验证Redis的token
                Object userId = redisTemplateSlave1.opsForValue().get(accessToken);
                if (userId == null) {
                    sendJsonMessage(response, JsonData.buildError(-5, "登录过期，请重新登录！"));
                    requestContext.setSendZuulResponse(false);
                    return null;
                }
//                Object latest_token =  redisTemplateSlave2.opsForValue().get(userId.toString());
//                if (!latest_token.equals(accessToken)){
//                    sendJsonMessage(response, JsonData.buildError(-5,"登录过期，请重新登录！"));
//                    return false;
//                }
                Claims claims = JWTUtils.checkJWT(accessToken);
                if (claims != null) {
                    Integer id = (Integer) claims.get("id");
                    String name = (String) claims.get("name");

                    requestContext.addZuulRequestHeader("user_id", id.toString());
                    requestContext.addZuulRequestHeader("name", name);
                    log.info("登陆校验成功：userId = [{}],name = [{}]", id, name);
                    return null;
                }
                sendJsonMessage(response, JsonData.buildError(-5, "登录过期，请重新登录！"));
                requestContext.setSendZuulResponse(false);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendJsonMessage(response, JsonData.buildError(-5, "登录过期，请重新登录！"));
        requestContext.setSendZuulResponse(false);
        return null;
    }

    private static void sendJsonMessage(HttpServletResponse response, Object object) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            response.setContentType("application/json;charset=utf-8");

            PrintWriter writer = response.getWriter();
            writer.print(objectMapper.writeValueAsString(object));
            writer.close();
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
