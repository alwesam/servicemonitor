package com.vandevsam.service.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestCallerRequests.class, TestConnections.class,
		TestObjectFields.class })
public class AllTests {

}
