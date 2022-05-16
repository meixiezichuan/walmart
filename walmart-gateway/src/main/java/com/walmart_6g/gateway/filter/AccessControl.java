package com.walmart_6g.gateway.filter;

import com.walmart_6g.gateway.feign.UserService;
import entity.Response;
import entity.User;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 2021/11/2  7:56 下午
 *
 * @author yang hong
 */

@Component
public class AccessControl implements GlobalFilter, Ordered {
    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

    @Autowired
    private UserService userService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try{
            ServerHttpRequest httpRequest = exchange.getRequest();
            String token = exchange.getRequest().getHeaders().getFirst("token");
            String path = exchange.getRequest().getURI().getPath();
            String[] path_units = path.split("/");
            // 如果有pathVariable后缀，则去除
            if (path_units[path_units.length-1].matches("^[0-9]+$") || path_units[path_units.length-1].length() ==32
            || path_units[path_units.length-1].length() ==36){
                StringBuffer s = new StringBuffer();
                for (int i=1; i< path_units.length-1; i++){
                    s.append("/").append(path_units[i]);
                }
                path = s.toString();
            }
            String uri = exchange.getRequest().getMethodValue() + path;
            ResponseEntity<Response> response = userService.verify(uri, token);
            if (response.getStatusCodeValue()== 200){
                if (token != null){
                    ResponseEntity<Response> userByToken = userService.getUserByToken(token);
                    LinkedHashMap user = (LinkedHashMap) userByToken.getBody().getData();
                    if (user != null){
                        System.out.println(user.get("id"));
                        ServerHttpRequest.Builder requestBuilder = httpRequest.mutate();
                        requestBuilder.headers(k -> k.set("userId",  user.get("id").toString()));
                        requestBuilder.headers(k -> k.set("role", user.get("role").toString()));
                        ServerHttpRequest request = requestBuilder.build();
                        exchange.mutate().request(request).build();
                    }
                }
                return chain.filter(exchange);
            }
        }catch (Exception e){
            DataBuffer buffer;
            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            serverHttpResponse.getHeaders().add("Content-Type", "application/json");
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            buffer = serverHttpResponse.bufferFactory().wrap("not authoried or token is expire".getBytes(StandardCharsets.UTF_8));
            return serverHttpResponse.writeWith(Mono.just(buffer));
        }
        DataBuffer buffer;
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.getHeaders().add("Content-Type", "application/json");
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        buffer = serverHttpResponse.bufferFactory().wrap("not authoried or token is expire".getBytes(StandardCharsets.UTF_8));
        return serverHttpResponse.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
