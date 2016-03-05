package net.openright.simpleserverseed.application;

import net.openright.simpleserverseed.domain.customers.Customer;
import net.openright.simpleserverseed.domain.customers.CustomerRepositoryTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerApiControllerTest {

    @Test
    public void shouldConvertToAndFromJson() {
        Customer customer = CustomerRepositoryTest.sampleCustomer();

        assertThat(Customer.fromJson(customer.toJson()))
                .isEqualToComparingFieldByField(customer);
    }

    @Test
    public void shouldBeAbleToListCreatedCustomers() {
        CustomerApiController controller = new CustomerApiController(SimpleseedTestConfig.instance());

        Customer customer = CustomerRepositoryTest.sampleCustomer();
        controller.createResource(customer.toJson());

        assertThat(controller.listResources().requiredArray("customers"))
                .contains(customer.toJson());
    }


}