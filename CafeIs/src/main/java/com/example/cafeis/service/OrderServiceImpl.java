package com.example.cafeis.service;

import com.example.cafeis.DTO.*;
import com.example.cafeis.Domain.*;
import com.example.cafeis.Enum.OrderStatus;
import com.example.cafeis.Enum.Priority;
import com.example.cafeis.exception.MemberNotFoundException;
import com.example.cafeis.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository registrantRepository;
    private final ProductRepository productRepository;
    private final BasketRepository basketRepository;
    private final BranchRepository branchRepository;

    @Override
    public Long registerPurchaseRequest(OrderCreateDTO orderCreateDTO) {

        Registrant registrant = findMemberByEmail(orderCreateDTO.getRegistrantEmail());
        Branch branch = findBranchById(orderCreateDTO.getBranchNo());

        Order order = createOrderEntity(orderCreateDTO, registrant, branch);

        orderCreateDTO.getOrderItems().stream()
                .map(this::createOrderItemFromDTO)
                .peek(OrderItem::extractMerchandiseDetails)
                .forEach(order::attachPurchaseUnit);

        order.calculateTotalAmount();

        return Optional.of(order)
                .map(orderRepository::save)
                .map(Order::getOrderNo)
                .orElseThrow(() -> new RuntimeException("주문 저장 실패"));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO fetchRequestDetails(Long orderNo) {
        return orderRepository.findByIdWithItems(orderNo)
                .map(this::entityToDTO)
                .orElseThrow(() -> generateOrderNotFoundException(orderNo));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListDTO> queryByProgressMarker(String email, Pageable pageable) {
        return orderRepository.findByMemberEmailWithItemCount(email, pageable);
    }

    @Override
    public void adjustRequestProgress(OrderStatusUpdateDTO updateDTO) {
        findOrderById(updateDTO.getOrderNo())
                .ifPresent(order -> {
                    order.adjustRequestProgress(updateDTO.getOrderStatus());

                    Optional.ofNullable(updateDTO.getConfirmedPickupTime())
                            .ifPresent(order::lockPickupSchedule);

                    Optional.ofNullable(updateDTO.getCancelReason())
                            .ifPresent(order::revokePurchaseRequest);

                    orderRepository.save(order);
                });
    }

    @Override
    public void finalizeOfflinePayment(Long orderNo) {
        findOrderById(orderNo)
                .map(order -> {
                    order.finalizeOfflinePayment();
                    return orderRepository.save(order);
                })
                .ifPresentOrElse(
                        order -> {},
                        () -> { throw generateOrderNotFoundException(orderNo); }
                );
    }

    @Override
    public void revokePurchaseRequest(Long orderNo, String reason) {
        processOrderWithLogging(orderNo,
                order -> order.revokePurchaseRequest(reason),
                "주문이 취소되었습니다: {} - 사유: {}",
                orderNo, reason);
    }

    @Override
    public void acknowledgeArrival(Long orderNo) {
        processOrderWithLogging(orderNo,
                Order::acknowledgeArrival,
                "주문이 수령되었습니다: {}",
                orderNo);
    }

    @Override
    public void lockPickupSchedule(Long orderNo, LocalDateTime confirmedTime) {
        processOrderWithLogging(orderNo,
                order -> order.lockPickupSchedule(confirmedTime),
                "주문의 픽업 시간이 확정되었습니다: {} - 시간: {}",
                orderNo, confirmedTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderListDTO> listScheduledPickupsByDate(LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

        OrderStatus pickupStatus = OrderStatus.READY_FOR_PICKUP;

        return orderRepository.findOrdersByPickupTimeRange(startOfDay, endOfDay, pickupStatus).stream()
                .map(this::entityToListDTO)
                .toList();
    }

    @Override
    public List<EmployeeOrderDTO> getOrdersByTimeWindow(int startMinutes, int endMinutes) {
        TimeRange timeRange = calculateTimeRange(startMinutes, endMinutes);

        return orderRepository.findOrdersByPickupTimeRange(
                        timeRange.start(), timeRange.end(), OrderStatus.LOCKEDPHASE
                ).stream()
                .peek(order -> {})
                .map(this::convertToEmployeeDto)
                .toList();
    }

    @Override
    @Transactional
    public Long createOrderFromBasket(String registrantEmail, LocalDateTime requestedPickupTime, String specialRequests) {

        try {
            Registrant registrant = findMemberByEmail(registrantEmail);

            Basket basket = findBasketWithItems(registrantEmail);

            validateBasket(basket);

            Order order = createOrderFromBasketData(registrant, basket, requestedPickupTime, specialRequests);

            basket.getBasketGoodsPerson().stream()
                    .map(this::convertBasketItemToOrderItem)
                    .peek(OrderItem::extractMerchandiseDetails)
                    .forEach(order::attachPurchaseUnit);

            order.calculateTotalAmount();
            Order savedOrder = orderRepository.save(order);

            clearBasket(basket);

            return savedOrder.getOrderNo();
        } catch (Exception e) {
            throw e;
        }
    }

    private record TimeRange(LocalDateTime start, LocalDateTime end) {}

    private TimeRange calculateTimeRange(int startMinutes, int endMinutes) {
        LocalDateTime now = LocalDateTime.now();
        return new TimeRange(
                now.plusMinutes(startMinutes),
                now.plusMinutes(endMinutes)
        );
    }

    private Registrant findMemberByEmail(String email) {
        return registrantRepository.findById(email)
                .orElseThrow(() -> new MemberNotFoundException(email));
    }

    private Branch findBranchById(Long branchNo) {
        return branchRepository.findById(branchNo)
                .orElseThrow(() -> new RuntimeException("Store not found: " + branchNo));
    }

    private Optional<Order> findOrderById(Long orderNo) {
        return orderRepository.findById(orderNo);
    }

    private Order createOrderEntity(OrderCreateDTO dto, Registrant registrant, Branch branch) {
        return Order.builder()
                .registrant(registrant)
                .branch(branch)
                .requestedPickupTime(dto.getRequestedPickupTime())
                .specialRequests(dto.getSpecialRequests())
                .discountAmount(dto.getDiscountAmount())
                .build();
    }

    private OrderItem createOrderItemFromDTO(OrderCreateDTO.OrderItemCreateDTO itemDTO) {
        Product product = productRepository.findById(itemDTO.getProductNo())
                .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductNo()));

        return OrderItem.builder()
                .product(product)
                .quantity(itemDTO.getQuantity())
                .size(itemDTO.getSize())
                .temperature(itemDTO.getTemperature())
                .additionalOptions(itemDTO.getAdditionalOptions())
                .additionalPrice(itemDTO.getAdditionalPrice())
                .build();
    }

    private void processOrderWithLogging(Long orderNo,
                                         java.util.function.Consumer<Order> processor,
                                         String logMessage,
                                         Object... logParams) {
        findOrderById(orderNo)
                .map(order -> {
                    processor.accept(order);
                    return orderRepository.save(order);
                })
                .ifPresentOrElse(
                        order -> {},
                        () -> { throw generateOrderNotFoundException(orderNo); }
                );
    }

    private Basket findBasketWithItems(String registrantEmail) {
        return basketRepository.findByMemberEmailWithItems(registrantEmail)
                .orElseThrow(() -> new RuntimeException("Basket not found for registrant: " + registrantEmail));
    }

    private void validateBasket(Basket basket) {
        if (basket.getBasketGoodsPerson().isEmpty()) {
            throw new RuntimeException("Basket is empty for registrant: " + basket.getRegistrant().getEmail());
        }

        Optional.ofNullable(basket.getBranch())
                .orElseThrow(() -> new RuntimeException("No branch associated with basket"));
    }

    private Order createOrderFromBasketData(Registrant registrant, Basket basket,
                                            LocalDateTime requestedPickupTime,
                                            String specialRequests) {
        return Order.builder()
                .registrant(registrant)
                .branch(basket.getBranch())
                .requestedPickupTime(requestedPickupTime)
                .specialRequests(specialRequests)
                .build();
    }

    private OrderItem convertBasketItemToOrderItem(BasketGoods basketItem) {
        return OrderItem.builder()
                .product(basketItem.getProduct())
                .quantity(basketItem.getQuantity())
                .size(basketItem.getSize())
                .temperature(basketItem.getTemperature())
                .additionalOptions(basketItem.getAdditionalOptions())
                .additionalPrice(basketItem.getAdditionalPrice())
                .build();
    }

    private void clearBasket(Basket basket) {
        basketRepository.delete(basket);
    }

    private RuntimeException generateOrderNotFoundException(Long orderNo) {
        return new RuntimeException("Order not found: " + orderNo);
    }

    private Integer calculateRemainingMinutes(LocalDateTime pickupTime, LocalDateTime now) {
        return Optional.ofNullable(pickupTime)
                .map(time -> Duration.between(now, time).toMinutes())
                .filter(minutes -> minutes > 0)
                .map(Long::intValue)
                .orElse(0);
    }

    private Priority determinePriority(Order order) {
        return Optional.ofNullable(order.getConfirmedPickupTime())
                .map(pickupTime -> {
                    long minutesUntilPickup = Duration.between(LocalDateTime.now(), pickupTime).toMinutes();

                    return Stream.of(
                                    new PriorityRule(minutes -> minutes < 0, Priority.URGENT),
                                    new PriorityRule(minutes -> minutes <= 5, Priority.HIGH),
                                    new PriorityRule(minutes -> minutes <= 10, Priority.NORMAL)
                            )
                            .filter(rule -> rule.condition.test(minutesUntilPickup))
                            .map(rule -> rule.priority)
                            .findFirst()
                            .orElse(Priority.LOW);
                })
                .orElse(Priority.NORMAL);
    }

    private record PriorityRule(java.util.function.Predicate<Long> condition, Priority priority) {}

    private OrderDTO entityToDTO(Order order) {
        return OrderDTO.builder()
                .orderNo(order.getOrderNo())
                .registrantEmail(order.getRegistrant().getEmail())
                .orderTime(order.getOrderTime())
                .orderDate(order.getOrderDate())
                .requestedPickupTime(order.getRequestedPickupTime())
                .confirmedPickupTime(order.getConfirmedPickupTime())
                .orderStatus(order.getOrderStatus().name())
                .finalizedCost(order.getFinalizedCost())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .cashPaid(order.isCashPaid())
                .cashPaymentDate(order.getCashPaymentDate())
                .specialRequests(order.getSpecialRequests())
                .receivedDate(order.getReceivedDate())
                .orderItems(order.getOrderItems().stream()
                        .map(this::itemEntityToDTO)
                        .toList())
                .build();
    }

    private OrderListDTO entityToListDTO(Order order) {
        return OrderListDTO.builder()
                .orderNo(order.getOrderNo())
                .registrantEmail(order.getRegistrant().getEmail())
                .orderTime(order.getOrderTime())
                .orderStatus(order.getOrderStatus())
                .finalAmount(order.getFinalAmount())
                .cashPaid(order.isCashPaid())
                .requestedPickupTime(order.getRequestedPickupTime())
                .confirmedPickupTime(order.getConfirmedPickupTime())
                .itemCount((long) order.getOrderItems().size())
                .build();
    }

    private OrderItemDTO itemEntityToDTO(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .orderItemNo(orderItem.getOrderItemNo())
                .productNo(orderItem.getProduct().getProductNo())
                .productName(orderItem.getProductName())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .productImage(orderItem.getProductImage())
                .itemStatus(orderItem.getItemStatus().name())
                .build();
    }

    private EmployeeOrderDTO convertToEmployeeDto(Order order) {
        LocalDateTime now = LocalDateTime.now();

        return EmployeeOrderDTO.builder()
                .orderNo(order.getOrderNo())
                .registrantEmail(order.getRegistrant().getEmail())
                .confirmedPickupTime(order.getConfirmedPickupTime())
                .finalAmount(order.getFinalAmount())
                .itemCount((long) order.getOrderItems().size())
                .orderStatus(order.getOrderStatus())
                .remainingMinutes(calculateRemainingMinutes(order.getConfirmedPickupTime(), now))
                .priority(determinePriority(order).name())
                .build();
    }
}

