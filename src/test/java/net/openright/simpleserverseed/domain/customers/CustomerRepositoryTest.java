package net.openright.simpleserverseed.domain.customers;

import net.openright.infrastructure.test.SampleData;

public class CustomerRepositoryTest {
    public static Customer sampleCustomer() {
        Customer customer = new Customer();
        customer.setName(SampleData.sampleString(4));
        customer.setEmail(SampleData.sampleString(1) + "@" + SampleData.sampleString(1) + ".com");
        return customer;
    }
}
