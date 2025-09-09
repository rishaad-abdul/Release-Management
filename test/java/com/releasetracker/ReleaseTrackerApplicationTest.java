package com.releasetracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ReleaseTrackerApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully
        // It's a smoke test to ensure all beans are configured correctly
    }
}