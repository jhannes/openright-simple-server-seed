package net.openright.simpleserverseed.domain.products;

import net.openright.infrastructure.db.Database;
import net.openright.infrastructure.db.Database.Row;
import net.openright.infrastructure.rest.RequestException;
import net.openright.simpleserverseed.application.SeedAppConfig;

import java.sql.SQLException;
import java.util.List;

public class ProductRepository {

    private final Database db;

    public ProductRepository(SeedAppConfig database) {
        db = database.getDatabase();
    }

    public void insert(Product product) {
        product.setId(db.getNextValue("idgenerator"));
        db.insert("insert into products (id, price, active, description, title) values (?,?,?,?,?)",
                product.getId(), product.getPrice(), product.isActive(), product.getDescription(), product.getTitle());
    }

    public Product retrieve(long id) {
        return db.queryForSingle("select * from products where id = ?", id, ProductRepository::toProduct)
                .orElseThrow(() -> new RequestException(404, "Order " + id + " not found"));
    }

    public List<Product> list() {
        return db.queryForList(
                "select * from products where active = ? order by title",
                ProductRepository::toProduct, true);
    }

    public void update(Long id, Product product) {
        db.executeOperation("update products set price = ?, active = ?, description = ?, title = ? where id = ?",
                product.getPrice(), product.isActive(), product.getDescription(), product.getTitle(), id);
    }

    public static Product toProduct(Row rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("products", "id"));
        product.setTitle(rs.getString("products", "title"));
        product.setDescription(rs.getString("products", "description"));
        product.setActive(rs.getBoolean("products", "active"));
        product.setPrice(rs.getDouble("products", "price"));
        return product;
    }

}
