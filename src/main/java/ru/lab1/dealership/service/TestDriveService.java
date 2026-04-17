package ru.lab1.dealership.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.persistence.entity.CarInStockEntity;
import ru.lab1.dealership.persistence.entity.TestDriveRequestEntity;
import ru.lab1.dealership.persistence.entity.UserEntity;
import ru.lab1.dealership.persistence.repository.CarInStockRepository;
import ru.lab1.dealership.persistence.repository.TestDriveRequestRepository;
import ru.lab1.dealership.persistence.repository.UserRepository;
import ru.lab1.dealership.web.dto.TestDriveRequestDto;
import ru.lab1.dealership.web.dto.TestDriveResponse;

@Service
@Transactional
public class TestDriveService {
  private final TestDriveRequestRepository testDriveRequestRepository;
  private final UserRepository userRepository;
  private final CarInStockRepository carInStockRepository;

  public TestDriveService(
      TestDriveRequestRepository testDriveRequestRepository,
      UserRepository userRepository,
      CarInStockRepository carInStockRepository) {
    this.testDriveRequestRepository = testDriveRequestRepository;
    this.userRepository = userRepository;
    this.carInStockRepository = carInStockRepository;
  }

  @Transactional(readOnly = true)
  public List<TestDriveResponse> list() {
    return testDriveRequestRepository.findAllByRemovedFalse().stream()
        .map(this::toResponse)
        .toList();
  }

  public TestDriveResponse create(TestDriveRequestDto request) {
    if (request == null
        || request.clientId() == null
        || request.carId() == null
        || request.startDateTime() == null) {
      throw new DomainValidationException("Invalid test drive request.");
    }
    if (request.startDateTime().isBefore(Instant.now())) {
      throw new DomainValidationException("Test drive date must be in the future.");
    }
    UserEntity client =
        userRepository
            .findByIdAndRemovedFalse(request.clientId())
            .orElseThrow(EntityLookupSupport.notFound("User", request.clientId()));
    if (client.getRole() != UserRole.CLIENT) {
      throw new DomainValidationException("Only client can request a test drive.");
    }
    CarInStockEntity car =
        carInStockRepository
            .findByIdAndRemovedFalse(request.carId())
            .orElseThrow(EntityLookupSupport.notFound("Car in stock", request.carId()));
    if (!car.isAvailableForTestDrive()) {
      throw new DomainValidationException("Car is not available for test drive.");
    }
    TestDriveRequestEntity entity = new TestDriveRequestEntity();
    entity.setClient(client);
    entity.setCar(car);
    entity.setStartDateTime(request.startDateTime());
    return toResponse(testDriveRequestRepository.save(entity));
  }

  public void allowCar(UUID carId) {
    CarInStockEntity entity =
        carInStockRepository
            .findByIdAndRemovedFalse(carId)
            .orElseThrow(EntityLookupSupport.notFound("Car in stock", carId));
    entity.setAvailableForTestDrive(true);
    carInStockRepository.save(entity);
  }

  public void disallowCar(UUID carId) {
    CarInStockEntity entity =
        carInStockRepository
            .findByIdAndRemovedFalse(carId)
            .orElseThrow(EntityLookupSupport.notFound("Car in stock", carId));
    entity.setAvailableForTestDrive(false);
    carInStockRepository.save(entity);
  }

  private TestDriveResponse toResponse(TestDriveRequestEntity entity) {
    return new TestDriveResponse(
        entity.getId(),
        entity.getClient().getId(),
        entity.getCar().getId(),
        entity.getStartDateTime(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.isRemoved());
  }
}
