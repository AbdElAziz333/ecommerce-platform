package com.aziz.order.util;

import com.aziz.order.dto.request.CartOwner;
import com.aziz.order.util.exception.BadRequestException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

public class CartOwnerResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter p) {
        return CartOwner.class.equals(p.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter p, ModelAndViewContainer mav,
                                  NativeWebRequest request, WebDataBinderFactory f) {
        String user = request.getHeader("User-Id");
        String guest = request.getHeader("Session-Id");

        if (user == null && guest == null) {
            throw new BadRequestException("Authentication or session required");
        }
        try {
            return new CartOwner(user == null ? null : Long.valueOf(user), guest);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid user id");
        }
    }
}