package net.openright.simpleserverseed.domain.customers;

import org.jsonbuddy.JsonObject;

public class Customer {
    private String name;
    private String email;
    private int id;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id)
                .put("name", name)
                .put("email", email);
    }

    public static Customer fromJson(JsonObject jsonObject) {
        Customer customer = new Customer();
        jsonObject.longValue("id").ifPresent(customer::setId);
        customer.setName(jsonObject.requiredString("name"));
        customer.setEmail(jsonObject.requiredString("email"));
        return customer;
    }

    private void setId(long id) {
        this.id = (int)id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
