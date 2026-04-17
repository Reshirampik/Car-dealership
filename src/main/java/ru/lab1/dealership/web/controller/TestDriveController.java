package ru.lab1.dealership.web.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.lab1.dealership.service.TestDriveService;
import ru.lab1.dealership.web.dto.TestDriveRequestDto;
import ru.lab1.dealership.web.dto.TestDriveResponse;

@RestController
@RequestMapping("/api/test-drives")
public class TestDriveController {
  private final TestDriveService testDriveService;

  public TestDriveController(TestDriveService testDriveService) {
    this.testDriveService = testDriveService;
  }

  @GetMapping
  public List<TestDriveResponse> list() {
    return testDriveService.list();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TestDriveResponse create(@RequestBody TestDriveRequestDto request) {
    return testDriveService.create(request);
  }

  @PostMapping("/cars/{carId}/allow")
  public void allow(@PathVariable UUID carId) {
    testDriveService.allowCar(carId);
  }

  @PostMapping("/cars/{carId}/disallow")
  public void disallow(@PathVariable UUID carId) {
    testDriveService.disallowCar(carId);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    // soft delete endpoint can be added later
  }
}
