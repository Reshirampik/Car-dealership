package ru.lab1.dealership.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.order.CommonOrderStatus;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.persistence.entity.CarInStockEntity;
import ru.lab1.dealership.persistence.entity.CommonOrderEntity;
import ru.lab1.dealership.persistence.entity.UserEntity;
import ru.lab1.dealership.persistence.repository.CarInStockRepository;
import ru.lab1.dealership.persistence.repository.CommonOrderRepository;
import ru.lab1.dealership.persistence.repository.UserRepository;
import ru.lab1.dealership.web.dto.CreateCommonOrderRequest;
import ru.lab1.dealership.web.dto.OrderResponse;

@Service
@Transactional
public class CommonOrderService {
  private final CommonOrderRepository commonOrderRepository;
  private final UserRepository userRepository;
  private final CarInStockRepository carInStockRepository;

  public CommonOrderService(
      CommonOrderRepository commonOrderRepository,
      UserRepository userRepository,
      CarInStockRepository carInStockRepository) {
    this.commonOrderRepository = commonOrderRepository;
    this.userRepository = userRepository;
    this.carInStockRepository = carInStockRepository;
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> list() {
    return commonOrderRepository.findAllByRemovedFalse().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public OrderResponse get(UUID id) {
    return toResponse(
        commonOrderRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Common order", id)));
  }

  public OrderResponse create(CreateCommonOrderRequest request) {
    if (request == null || request.clientId() == null || request.carId() == null) {
      throw new DomainValidationException("Invalid common order request.");
    }
    UserEntity client =
        userRepository
            .findByIdAndRemovedFalse(request.clientId())
            .orElseThrow(EntityLookupSupport.notFound("User", request.clientId()));
    if (client.getRole() != UserRole.CLIENT) {
      throw new DomainValidationException("Only client can create a common order.");
    }
    CarInStockEntity car =
        carInStockRepository
            .findByIdAndRemovedFalse(request.carId())
            .orElseThrow(EntityLookupSupport.notFound("Car in stock", request.carId()));
    UserEntity manager =
        userRepository.findAllByRemovedFalse().stream()
            .filter(user -> user.getRole() == UserRole.MANAGER)
            .findFirst()
            .orElseThrow(() -> new DomainValidationException("No manager available."));
    CommonOrderEntity entity = new CommonOrderEntity();
    entity.setClient(client);
    entity.setCar(car);
    entity.setManager(manager);
    entity.setTotalPrice(car.getPrice());
    entity.setStatus(CommonOrderStatus.CREATED);
    return toResponse(commonOrderRepository.save(entity));
  }

  public OrderResponse approve(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.approveByManager();
    return toResponse(commonOrderRepository.save(entity));
  }

  public OrderResponse waitPayment(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.moveToWaitingPayment();
    return toResponse(commonOrderRepository.save(entity));
  }

  public OrderResponse markPaid(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.markPaid();
    return toResponse(commonOrderRepository.save(entity));
  }

  public OrderResponse readyForPickup(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.markReadyForPickup();
    return toResponse(commonOrderRepository.save(entity));
  }

  public OrderResponse complete(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.complete();
    return toResponse(commonOrderRepository.save(entity));
  }

  public OrderResponse cancel(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.cancel();
    return toResponse(commonOrderRepository.save(entity));
  }

  public void delete(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.setRemoved(true);
    commonOrderRepository.save(entity);
  }

  private CommonOrderEntity require(UUID id) {
    return commonOrderRepository
        .findByIdAndRemovedFalse(id)
        .orElseThrow(EntityLookupSupport.notFound("Common order", id));
  }

  private OrderResponse toResponse(CommonOrderEntity entity) {
    return new OrderResponse(
        entity.getId(),
        entity.getStatus(),
        entity.getClient().getId(),
        entity.getManager().getId(),
        entity.getCar().getId(),
        entity.getCar().getModel().displayName(),
        entity.getTotalPrice(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.isRemoved());
  }
}
