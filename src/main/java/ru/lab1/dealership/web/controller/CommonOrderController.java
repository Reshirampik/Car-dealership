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
import ru.lab1.dealership.service.CommonOrderService;
import ru.lab1.dealership.web.dto.CreateCommonOrderRequest;
import ru.lab1.dealership.web.dto.OrderResponse;

@RestController
@RequestMapping("/api/common-orders")
public class CommonOrderController {
  private final CommonOrderService commonOrderService;

  public CommonOrderController(CommonOrderService commonOrderService) {
    this.commonOrderService = commonOrderService;
  }

  @GetMapping
  public List<OrderResponse> list() {
    return commonOrderService.list();
  }

  @GetMapping("/{id}")
  public OrderResponse get(@PathVariable UUID id) {
    return commonOrderService.get(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrderResponse create(@RequestBody CreateCommonOrderRequest request) {
    return commonOrderService.create(request);
  }

  @PostMapping("/{id}/approve")
  public OrderResponse approve(@PathVariable UUID id) {
    return commonOrderService.approve(id);
  }

  @PostMapping("/{id}/waiting-payment")
  public OrderResponse waitingPayment(@PathVariable UUID id) {
    return commonOrderService.waitPayment(id);
  }

  @PostMapping("/{id}/paid")
  public OrderResponse paid(@PathVariable UUID id) {
    return commonOrderService.markPaid(id);
  }

  @PostMapping("/{id}/ready")
  public OrderResponse ready(@PathVariable UUID id) {
    return commonOrderService.readyForPickup(id);
  }

  @PostMapping("/{id}/complete")
  public OrderResponse complete(@PathVariable UUID id) {
    return commonOrderService.complete(id);
  }

  @PostMapping("/{id}/cancel")
  public OrderResponse cancel(@PathVariable UUID id) {
    return commonOrderService.cancel(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    commonOrderService.delete(id);
  }
}
