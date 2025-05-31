package com.techsage.banking.controllers;

import org.junit.jupiter.api.*;
import org.junit.platform.suite.api.*;

@Tag("integration")
@Suite
@SelectClasses({
        AuthControllerTest.class,
        UserControllerTest.class
})
public class ControllerTestSuite {
}
