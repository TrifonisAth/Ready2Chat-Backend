package com.socialmedia.socialmedia.interceptor;

import com.socialmedia.socialmedia.services.impl.JWTService;
import io.micrometer.common.lang.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

public class QueryParamHandshakeInterceptor extends HttpSessionHandshakeInterceptor implements HandshakeInterceptor, HandshakeHandler {
    private final JWTService jwtService;

    public QueryParamHandshakeInterceptor(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,@NonNull ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("beforeHandshake");
        String[] ticket = getUserFromRequest(request);
        System.out.println("ticket: " + ticket[0] + " " + ticket[1]);
        // check if the ip is the same as the one in the ticket.
        if (!request.getRemoteAddress().getHostString().equals(ticket[1])) {
            return false;
        }
        attributes.put("user", ticket);
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,@NonNull ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        super.afterHandshake(request, response, wsHandler, exception);
    }

    private String[] getUserFromRequest(ServerHttpRequest request) {
        String ticket = request.getURI().getQuery();
        System.out.println("ticket: " + ticket);
        return jwtService.getUserIdAndIpFromTicket(ticket);
    }

    @Override
    public boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws HandshakeFailureException {
        return true;
    }
}
