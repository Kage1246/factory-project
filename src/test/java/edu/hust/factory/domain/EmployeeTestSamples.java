package edu.hust.factory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EmployeeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Employee getEmployeeSample1() {
        return new Employee()
            .id(1L)
            .employeeCode("employeeCode1")
            .username("username1")
            .hashPassword("hashPassword1")
            .name("name1")
            .phone("phone1")
            .email("email1")
            .note("note1")
            .status(1);
    }

    public static Employee getEmployeeSample2() {
        return new Employee()
            .id(2L)
            .employeeCode("employeeCode2")
            .username("username2")
            .hashPassword("hashPassword2")
            .name("name2")
            .phone("phone2")
            .email("email2")
            .note("note2")
            .status(2);
    }

    public static Employee getEmployeeRandomSampleGenerator() {
        return new Employee()
            .id(longCount.incrementAndGet())
            .employeeCode(UUID.randomUUID().toString())
            .username(UUID.randomUUID().toString())
            .hashPassword(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString())
            .status(intCount.incrementAndGet());
    }
}
