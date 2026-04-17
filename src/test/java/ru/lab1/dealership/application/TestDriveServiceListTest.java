package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.*;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.infrastructure.repository.TestDriveRequestRepository;

class TestDriveServiceListTest {

  @Test
  void shouldListRequests() {
    var repo = new TestDriveRequestRepository();
    var service = new TestDriveService(repo);

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

    service.createRequest(client, car, LocalDateTime.now().plusDays(1));
    service.createRequest(client, car, LocalDateTime.now().plusDays(2));

    assertEquals(2, service.listRequests().size());
  }
}
