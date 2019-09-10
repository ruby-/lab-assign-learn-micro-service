import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import edu.uta.cse.thrift.user.dto.UserDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

/**
 * (Demo: java11 net features)
 * Filter the http request:
 * 1) if user hasn't login, redirect to login
 * 2) if already login, check token and get user info
 */
public abstract class LoginFilter implements Filter {

    protected abstract void login(HttpServletRequest request, HttpServletResponse response, UserDTO userDTO);

    /* Guava cache example */
    private static Cache<String, UserDTO> cache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build();

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String token = request.getParameter("token");
        if (token == null || StringUtils.isBlank(token)) {
            Cookie[] cookies = request.getCookies();//support browser cookie for saved token on user-side
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals("token")) {
                        token = c.getValue();
                    }
                }
            }

        }

        //get user info is token is not empty
        UserDTO userDTO = null;
        if (token != null && !StringUtils.isBlank(token)) {
            userDTO = cache.getIfPresent(token);//use guava cache
            if (userDTO == null) {
                userDTO = requestUserInfo(token);
                if (userDTO != null) {
                    cache.put(token, userDTO);
                }
            }
        }

        if (userDTO == null) {
            /* Service redirect by Zuul api gateway demo
            * use: edit hosts, add 127.0.0.1 -> www.microservicedemo.com
            * */
            response.sendRedirect("http://www.microservicedemo.com/user/login");
            return;
        }


        login(request, response, userDTO);

        filterChain.doFilter(request, response);
    }


    private UserDTO requestUserInfo(String token) {
        String url = "http://user-service:8082/user/authentication";

        var client = HttpClient.newHttpClient();
        var post = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .header("token", token).build();
        try {
            var response = client.send(post, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {//i.e. code!=200
                throw new RuntimeException("request user info failed! statusLine:" + response.statusCode());
            }
            var userDTO = new ObjectMapper().readValue(response.body(), UserDTO.class);//java11 directly process response
            return userDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void destroy() {
    }
}
