package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.*;
import ru.lab1.dealership.domain.order.CommonOrderStatus;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.infrastructure.repository.CommonOrderRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class OrderServiceTest {

  @Test
  void shouldCreateOrderWithAutoAssignedManager() {
    var orderRepo = new CommonOrderRepository();

    var userRepo = new UserRepository();
    var manager = new User(UUID.randomUUID(), "Manager", User.Role.MANAGER);
    userRepo.save(manager);

    var assign = new ManagerAssignmentService(userRepo, new Random(1));
    var service = new OrderService(orderRepo, assign);

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

    var car = new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("3600000"));

    var order = service.createOrder(client, car);

    assertEquals(CommonOrderStatus.CREATED, order.getStatus());
    assertEquals(client, order.getClient());
    assertEquals(User.Role.MANAGER, order.getManager().getRole());
  }
}
