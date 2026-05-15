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
import ru.lab1.dealership.service.CustomOrderService;
import ru.lab1.dealership.web.dto.CreateCustomOrderRequest;
import ru.lab1.dealership.web.dto.CustomOrderResponse;

@RestController
@RequestMapping("/api/custom-orders")
public class CustomOrderController {
  private final CustomOrderService customOrderService;

  public CustomOrderController(CustomOrderService customOrderService) {
    this.customOrderService = customOrderService;
  }

  @GetMapping
  public List<CustomOrderResponse> list() {
    return customOrderService.list();
  }

  @GetMapping("/{id}")
  public CustomOrderResponse get(@PathVariable UUID id) {
    return customOrderService.get(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CustomOrderResponse create(@RequestBody CreateCustomOrderRequest request) {
    return customOrderService.create(request);
  }

  @PostMapping("/{id}/approve")
  public CustomOrderResponse approve(@PathVariable UUID id) {
    return customOrderService.approve(id);
  }

  @PostMapping("/{id}/waiting-payment")
  public CustomOrderResponse waitingPayment(@PathVariable UUID id) {
    return customOrderService.waitPayment(id);
  }

  @PostMapping("/{id}/paid")
  public CustomOrderResponse paid(@PathVariable UUID id) {
    return customOrderService.markPaid(id);
  }

  @PostMapping("/{id}/waiting-delivery")
  public CustomOrderResponse waitingDelivery(@PathVariable UUID id) {
    return customOrderService.waitDelivery(id);
  }

  @PostMapping("/{id}/ready")
  public CustomOrderResponse ready(@PathVariable UUID id) {
    return customOrderService.readyForPickup(id);
  }

  @PostMapping("/{id}/complete")
  public CustomOrderResponse complete(@PathVariable UUID id) {
    return customOrderService.complete(id);
  }

  @PostMapping("/{id}/cancel")
  public CustomOrderResponse cancel(@PathVariable UUID id) {
    return customOrderService.cancel(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    customOrderService.delete(id);
  }
}
