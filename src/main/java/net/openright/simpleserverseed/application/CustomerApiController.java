package net.openright.simpleserverseed.application;

import net.openright.infrastructure.rest.ResourceApi;
import net.openright.simpleserverseed.domain.customers.Customer;
import net.openright.simpleserverseed.domain.customers.CustomerRepository;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;

public class CustomerApiController implements ResourceApi {

    private final CustomerRepository repository;

    public CustomerApiController(SeedAppConfig config) {
        repository = new CustomerRepository(config);
    }

    @Override
    public String createResource(JsonObject jsonObject) {
        return String.valueOf(repository.save(Customer.fromJson(jsonObject)));
    }

    @Override
    public JsonObject listResources() {
        return new JsonObject()
            .put("customers", JsonArray.map(repository.list(), Customer::toJson));
    }
}
