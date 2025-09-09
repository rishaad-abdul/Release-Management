package com.releasetracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentTest {

    @Test
    void getNext_ReturnsCorrectEnvironment() {
        assertEquals(Environment.QA, Environment.DEV.getNext());
        assertEquals(Environment.UAT, Environment.QA.getNext());
        assertEquals(Environment.PROD, Environment.UAT.getNext());
        assertNull(Environment.PROD.getNext());
    }

    @Test
    void getPrevious_ReturnsCorrectEnvironment() {
        assertNull(Environment.DEV.getPrevious());
        assertEquals(Environment.DEV, Environment.QA.getPrevious());
        assertEquals(Environment.QA, Environment.UAT.getPrevious());
        assertEquals(Environment.UAT, Environment.PROD.getPrevious());
    }

    @Test
    void getDisplayName_ReturnsCorrectName() {
        assertEquals("Development", Environment.DEV.getDisplayName());
        assertEquals("Quality Assurance", Environment.QA.getDisplayName());
        assertEquals("User Acceptance Testing", Environment.UAT.getDisplayName());
        assertEquals("Production", Environment.PROD.getDisplayName());
    }
}