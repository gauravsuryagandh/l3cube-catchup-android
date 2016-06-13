package com.l3cube.catchup;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void stringComparison_isCorrect() throws Exception {
        Boolean isSame = false;
        String a,b;
        a = "abc";
        b = "AbC";
        isSame = a.equalsIgnoreCase(b);
        assertTrue("Strings are not same for equalsIgnoreCase",isSame);
    }
}