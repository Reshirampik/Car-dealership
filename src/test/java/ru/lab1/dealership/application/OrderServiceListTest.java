package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.*;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.infrastructure.repository.CommonOrderRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class OrderServiceListTest {

  @Test
  void shouldListOrders() {
    var orderRepo = new CommonOrderRepository();

    var userRepo = new UserRepository();
    userRepo.save(new User(UUID.randomUUID(), "M1", User.Role.MANAGER));

    var service =
        new OrderService(orderRepo, new ManagerAssignmentService(userRepo, new Random(1)));

    var client = new User(UUID.randomUUID(), "Ivan", User.Role.CLIENT);

    var model =
        new CarModel(
            UUID.randomUUID(),
            "BMW",
            "320i",
            BodyType.SEDAN,
            FuelType.GASOLINE,
            184,
            new BigDecimal("2.0"),
            GearboxType.AUTOMATIC,
            DriveType.RWD,
            new BigDecimal("3500000"));

    var car1 = new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("3600000"));
    var car2 = new CarInStock(UUID.randomUUID(), model, "white", new BigDecimal("3650000"));

    service.createOrder(client, car1);
    service.createOrder(client, car2);

    assertEquals(2, service.listOrders().size());
  }
}
