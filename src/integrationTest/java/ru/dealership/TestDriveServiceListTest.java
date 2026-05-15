package ru.dealership;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.lab1.dealership.DealershipApplication;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.config.ComponentType;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.persistence.entity.CarInStockEntity;
import ru.lab1.dealership.persistence.entity.CarModelEntity;
import ru.lab1.dealership.persistence.entity.UserEntity;
import ru.lab1.dealership.persistence.repository.CarInStockRepository;
import ru.lab1.dealership.persistence.repository.CarModelRepository;
import ru.lab1.dealership.persistence.repository.UserRepository;
import ru.lab1.dealership.service.TestDriveService;
import ru.lab1.dealership.web.dto.TestDriveRequestDto;

@Testcontainers
@SpringBootTest(classes = DealershipApplication.class)
@Transactional
class TestDriveServiceListTest {

  @Container
  static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("dealership")
          .withUsername("dealership")
          .withPassword("dealership");

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
  }

  @Autowired private TestDriveService service;
  @Autowired private UserRepository userRepository;
  @Autowired private CarModelRepository carModelRepository;
  @Autowired private CarInStockRepository carRepository;

  @Test
  void shouldListRequests() {
    UserEntity client = userRepository.save(new UserEntity("Client", UserRole.CLIENT));

    CarModelEntity model = new CarModelEntity();
    model.setBrand("Mercedes-Benz");
    model.setModelName("C 200");
    model.setBodyType(BodyType.SEDAN);
    model.setFuelType(FuelType.GASOLINE);
    model.setEnginePowerHp(204);
    model.setEngineVolumeLiters(new BigDecimal("1.5"));
    model.setGearboxType(GearboxType.AUTOMATIC);
    model.setDriveType(DriveType.RWD);
    model.setBasePrice(new BigDecimal("4500000"));
    model.setRequiredComponentTypes(Set.of(ComponentType.WHEELS, ComponentType.TRANSMISSION));
    model = carModelRepository.save(model);

    CarInStockEntity car = new CarInStockEntity();
    car.setModel(model);
    car.setColor("black");
    car.setPrice(new BigDecimal("4690000"));
    car.setAvailableForTestDrive(true);
    car = carRepository.save(car);

    service.create(
        new TestDriveRequestDto(client.getId(), car.getId(), Instant.now().plusSeconds(3600)));
    service.create(
        new TestDriveRequestDto(client.getId(), car.getId(), Instant.now().plusSeconds(7200)));

    assertEquals(2, service.list().size());
  }
}
