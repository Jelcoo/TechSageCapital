package com.techsage.banking.services;

import org.junit.platform.suite.api.*;

@Suite
@SelectClasses({
        AtmServiceJpaTest.class,
        BankAccountServiceJpaTest.class,
        StatisticsServiceJpaTest.class,
        TransactionServiceJpaTest.class,
        TurnstileServiceTest.class,
        UserDetailsServiceJpaTest.class,
        UserServiceJpaTest.class
})
public class ServiceTestSuite {
}
