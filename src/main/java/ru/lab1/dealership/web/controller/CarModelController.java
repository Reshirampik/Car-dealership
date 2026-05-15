package ru.lab1.dealership.web.controller;

import java.util.List;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.lab1.dealership.service.CarModelService;
import ru.lab1.dealership.web.dto.CarModelFilterRequest;
import ru.lab1.dealership.web.dto.CarModelRequest;
import ru.lab1.dealership.web.dto.CarModelResponse;

@RestController
@RequestMapping("/api/car-models")
public class CarModelController {
  private final CarModelService carModelService;

  public CarModelController(CarModelService carModelService) {
    this.carModelService = carModelService;
  }

  @GetMapping("/{id}")
  public CarModelResponse get(@PathVariable UUID id) {
    return carModelService.get(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CarModelResponse create(@RequestBody CarModelRequest request) {
    return carModelService.create(request);
  }

  @PutMapping("/{id}")
  public CarModelResponse update(@PathVariable UUID id, @RequestBody CarModelRequest request) {
    return carModelService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    carModelService.delete(id);
  }

  @GetMapping
  public List<CarModelResponse> list(@ParameterObject CarModelFilterRequest filter) {
    return carModelService.list(filter);
  }
}
