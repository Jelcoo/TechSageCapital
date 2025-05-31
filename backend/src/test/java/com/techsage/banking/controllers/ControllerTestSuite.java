package com.techsage.banking.controllers;

import org.junit.platform.suite.api.*;

@Suite
@SelectClasses({
        AuthControllerTest.class, // Always keep this at the top, else JWT does not get populated.
        AtmControllerTest.class,
        UserControllerTest.class
})
public class ControllerTestSuite {
}
