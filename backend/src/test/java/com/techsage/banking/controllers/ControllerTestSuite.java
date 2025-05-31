package com.techsage.banking.controllers;

import org.junit.platform.suite.api.*;

@Suite
@SelectClasses({
        AuthControllerTest.class,
        UserControllerTest.class
})
public class ControllerTestSuite {
}
