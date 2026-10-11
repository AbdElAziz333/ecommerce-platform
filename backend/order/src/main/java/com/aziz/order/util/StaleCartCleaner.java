package com.aziz.order.util;

import com.aziz.order.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaleCartCleaner {
    private static final Duration MAX_AGE = Duration.ofDays(30);
    private final CartRepository repository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purge() {
        int deleted = repository.deleteStaleGuestCarts(Instant.now().minus(MAX_AGE));
        log.info("Deleted {} stale guest carts", deleted);
    }
}