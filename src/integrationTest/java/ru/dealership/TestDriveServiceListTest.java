package ru.dealership;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.persistence.entity.CarInStockEntity;
import ru.lab1.dealership.persistence.entity.UserEntity;
import ru.lab1.dealership.persistence.repository.CarInStockRepository;
import ru.lab1.dealership.persistence.repository.UserRepository;
import ru.lab1.dealership.service.TestDriveService;
import ru.lab1.dealership.web.dto.TestDriveRequestDto;

@Testcontainers
@SpringBootTest
@Transactional
class TestDriveServiceListTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
  }

  @Autowired private TestDriveService service;
  @Autowired private UserRepository userRepository;
  @Autowired private CarInStockRepository carRepository;

  @Test
  void shouldListRequests() {
    var client = new UserEntity();
    client.setId(UUID.randomUUID());
    client.setRole(UserRole.CLIENT);
    userRepository.save(client);

    var car = new CarInStockEntity();
    car.setId(UUID.randomUUID());
    car.setAvailableForTestDrive(true);
    carRepository.save(car);

    service.create(
        new TestDriveRequestDto(client.getId(), car.getId(), Instant.now().plusSeconds(3600)));

    service.create(
        new TestDriveRequestDto(client.getId(), car.getId(), Instant.now().plusSeconds(7200)));

    assertEquals(2, service.list().size());
  }
}
