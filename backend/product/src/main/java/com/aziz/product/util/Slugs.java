package com.aziz.product.util;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.Locale;

public final class Slugs {
    private static final int MAX_BASE_LENGTH = 80;
    private static final int SUFFIX_LENGTH = 8;
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    private Slugs() {}

    /** "Blue T-Shirt" -> "blue-t-shirt-k3j9x0qa". The random suffix keeps slugs unique; the DB constraint is the safety net. */
    public static String from(String name) {
        String base = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")             // strip accents
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")      // drop special chars
                .replaceAll("[\\s-]+", "-")           // spaces/repeated hyphens -> one hyphen
                .replaceAll("^-|-$", "");

        if (base.length() > MAX_BASE_LENGTH) {
            base = base.substring(0, MAX_BASE_LENGTH).replaceAll("-+$", "");
        }

        if (base.isEmpty()) {
            base = "product";                         // name was all symbols or non-latin
        }

        return base + "-" + randomSuffix();
    }

    private static String randomSuffix() {
        StringBuilder sb = new StringBuilder(SUFFIX_LENGTH);

        for (int i = 0; i < SUFFIX_LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return sb.toString();
    }
}