package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.*;
import ru.lab1.dealership.domain.config.*;
import ru.lab1.dealership.domain.order.CustomOrderStatus;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.infrastructure.repository.CustomOrderRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class CustomOrderServiceTest {

  @Test
  void shouldCreateCustomOrderWithAutoAssignedManager() {
    var orderRepo = new CustomOrderRepository();

    var userRepo = new UserRepository();
    userRepo.save(new User(UUID.randomUUID(), "M1", User.Role.MANAGER));
    userRepo.save(new User(UUID.randomUUID(), "M2", User.Role.MANAGER));

    var assign = new ManagerAssignmentService(userRepo, new Random(1));
    var service = new CustomOrderService(orderRepo, assign);

    var client = new User(UUID.randomUUID(), "Ivan", User.Role.CLIENT);

    var modelId = UUID.randomUUID();
    var model =
        new CarModel(
            modelId,
            "BMW",
            "320i",
            BodyType.SEDAN,
            FuelType.GASOLINE,
            184,
            new BigDecimal("2.0"),
            GearboxType.AUTOMATIC,
            DriveType.RWD,
            new BigDecimal("3500000"));

    var config = new CarConfiguration(model);

    config.select(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.WHEELS,
            "19 M-Sport",
            new BigDecimal("95000"),
            Set.of(modelId)));
    config.select(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.TRANSMISSION,
            "8AT",
            new BigDecimal("0"),
            Set.of(modelId)));
    config.select(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.STEERING_WHEEL,
            "M-Sport heated",
            new BigDecimal("25000"),
            Set.of(modelId)));
    config.select(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.INTERIOR,
            "Dakota",
            new BigDecimal("110000"),
            Set.of(modelId)));

    var order = service.createOrder(client, config);

    assertEquals(CustomOrderStatus.CREATED, order.getStatus());
    assertEquals(client, order.getClient());
    assertEquals(User.Role.MANAGER, order.getManager().getRole());
  }
}
