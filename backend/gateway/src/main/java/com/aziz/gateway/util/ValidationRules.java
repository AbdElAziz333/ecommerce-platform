package com.aziz.gateway.util;

public final class ValidationRules {
    private ValidationRules() {}
    public static final String NOT_BLANK_REGEX = ".*\\S.*";
    public static final String PHONE_REGEX = "^\\+?[0-9]{11,13}$";
}