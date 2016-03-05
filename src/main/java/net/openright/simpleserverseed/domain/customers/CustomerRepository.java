package net.openright.simpleserverseed.domain.customers;

import net.openright.infrastructure.db.Database;
import net.openright.simpleserverseed.application.SeedAppConfig;

import java.util.Collection;

public class CustomerRepository {

    private final Database database;

    public CustomerRepository(SeedAppConfig config) {
        database = config.getDatabase();
    }

    public Collection<Customer> list() {
        return database.queryForList("select * from customers", row ->  {
            Customer customer = new Customer();
            customer.setName(row.getString("name"));
            customer.setEmail(row.getString("email"));
            return customer;
        });
    }

    public int save(Customer customer) {
        int id = database.insert("insert into customers (name, email) values (?, ?) returning id",
                customer.getName(), customer.getEmail());
        customer.setId(id);
        return id;
    }
}
