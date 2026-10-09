package com.aziz.gateway.dto.request;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

public final class TrustedHeadersRequest extends HttpServletRequestWrapper {
    private final Set<String> managed = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, String> trusted = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public TrustedHeadersRequest(HttpServletRequest request, Set<String> managedNames, Map<String, String> trustedValues) {
        super(request);
        managed.addAll(managedNames);
        trusted.putAll(trustedValues);
    }

    @Override
    public String getHeader(String name) {
        return managed.contains(name) ? trusted.get(name) : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (!managed.contains(name)) {
            return super.getHeaders(name);
        }

        String value = trusted.get(name);
        return value == null ? Collections.emptyEnumeration() : Collections.enumeration(List.of(value));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (String n : Collections.list(super.getHeaderNames())) {
            if (!managed.contains(n)) names.add(n);
        }

        names.addAll(trusted.keySet());
        return Collections.enumeration(names);
    }
}