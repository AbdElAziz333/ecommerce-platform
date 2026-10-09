package com.aziz.gateway.util;

public final class AddressRules {
    private AddressRules() {}
    public static final String CITY_REGEX = "ALEXANDRIA|EL_BEHEIRA|CAIRO|TANTA";
    public static final String CITY_MESSAGE = "must be one of ALEXANDRIA, EL_BEHEIRA, CAIRO, TANTA";
    public static final String NOT_BLANK_REGEX = ".*\\S.*";
}