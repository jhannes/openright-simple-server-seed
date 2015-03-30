package net.openright.simpleserverseed.domain.orders;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import net.openright.infrastructure.db.PgsqlDatabase;
import net.openright.simpleserverseed.application.SeedAppConfig;
import net.openright.simpleserverseed.application.SimpleseedTestConfig;

import org.junit.Test;

public class OrdersApiControllerTest {

	private SeedAppConfig config = new SimpleseedTestConfig();
	private DataSource dataSource = config.createDataSource();
	private PgsqlDatabase database = new PgsqlDatabase(dataSource);
	private OrdersRepository repository = new OrdersRepository(database);

	@Test
	public void shouldRetrieveSavedOrdersWithoutOrderLines() throws Exception {
		Order order = sampleOrder();
		repository.insert(order);
		assertThat(repository.list()).contains(order);

		assertThat(repository.retrieve(order.getId()))
			.isEqualToComparingFieldByField(order);
	}

	@Test
	public void shouldRetrieveSavedOrdersWithOrderLines() throws Exception {
		Order order = sampleOrder();

		order.addOrderLine("test");
		order.addOrderLine("test 2");

		repository.insert(order);

		assertThat(repository.retrieve(order.getId()))
			.isEqualToComparingFieldByField(order);
	}

	private static Random random = new Random();

	public static Order sampleOrder() {
		return new Order(sampleString(4));
	}

	private static String sampleString(int numberOfWords) {
		List<String> words = new ArrayList<String>();
		for (int i = 0; i < numberOfWords; i++) {
			words.add(sampleWord());
		}
		return String.join(" ", words);
	}

	private static String sampleWord() {
		return random(new String[] { "foo", "bar", "baz", "qux", "quux", "quuuux" });
	}

	private static <T> T random(@SuppressWarnings("unchecked") T... alternatives) {
		return alternatives[random.nextInt(alternatives.length)];
	}

}
