package com.bulkpurchase.web.controller.api;

import com.bulkpurchase.domain.dto.cart.CartItemOrderResponseDTO;
import com.bulkpurchase.domain.dto.order.OrderDetailResponseDTO;
import com.bulkpurchase.domain.dto.order.OrderResponseDTO;
import com.bulkpurchase.domain.dto.order.OrderViewDTO;
import com.bulkpurchase.domain.dto.order.PaymentResponseDTO;
import com.bulkpurchase.domain.entity.order.Order;
import com.bulkpurchase.domain.entity.user.User;
import com.bulkpurchase.domain.service.order.OrderDetailService;
import com.bulkpurchase.domain.service.order.OrderService;
import com.bulkpurchase.domain.service.order.PaymentService;
import com.bulkpurchase.domain.validator.user.UserAuthValidator;
import com.bulkpurchase.web.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderAPIController {

    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final PaymentService paymentService;
    private final UserAuthValidator userAuthValidator;
    private final PurchaseService purchaseService;

    @GetMapping("/list")
    public ResponseEntity<?> orderList(Principal principal) {
        User user = userAuthValidator.getCurrentUser(principal);
        List<OrderViewDTO> orderViewDTOS = orderService.getOrderViewModelsByUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(orderViewDTOS);
    }

    @GetMapping("/{orderID}")
    public ResponseEntity<?> orderDetail(@PathVariable("orderID") Long orderID) {
        Optional<Order> orderOpt = orderService.findById(orderID);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "잘못된 요청입니다."));
        }
        Order order = orderOpt.get();
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(order);
        PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO(paymentService.findByOrder(order));
        List<OrderDetailResponseDTO> detailResponseDTOS = orderDetailService.findByOrder(order).stream().map(OrderDetailResponseDTO::new).toList();

        return ResponseEntity.ok(Map.of("orderDetailList", detailResponseDTOS,"order", orderResponseDTO,"payment", paymentResponseDTO));
    }

    @GetMapping("/cart/{cartID}")
    public ResponseEntity<?> orderForm(@PathVariable("cartID") Long cartID,
                                       @RequestParam(value = "itemIDs", required = false) List<Long> itemIDs,
                                       @RequestParam(value = "productID", required = false) Long productID,
                                       @RequestParam(value = "quantity", required = false) Integer quantity,
                                       Principal principal) {
        User user = userAuthValidator.getCurrentUser(principal);

        if (!itemIDs.isEmpty()) {
            List<CartItemOrderResponseDTO> orderResponseDTOS = purchaseService.processCartPurchase(cartID, itemIDs, user);
            return ResponseEntity.ok(orderResponseDTOS);
        } else if (productID != null && quantity != null) {
            List<CartItemOrderResponseDTO> orderResponseDTOS = new ArrayList<>();
            CartItemOrderResponseDTO dto = purchaseService.processDirectPurchase(productID, quantity);
            orderResponseDTOS.add(dto);
            return ResponseEntity.ok(orderResponseDTOS);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "잘못된 요청입니다."));
        }
    }

//    @PostMapping("/direct-purchase")
//    public ResponseEntity<?> directPurchase(@RequestBody DirectPurchaseRequestDTO requestDTO, Principal principal) {
//        User user = userAuthValidator.getCurrentUser(principal);
//        DirectPurchaseResponseDTO responseDTO = purchaseService.processDirectPurchase(requestDTO, user);
//        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
//    }
}
