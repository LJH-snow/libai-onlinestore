package com.gzmtu.backend.controller;

import com.gzmtu.backend.dto.OrderDTO;
import com.gzmtu.backend.entity.Order;
import com.gzmtu.backend.service.OrderService;
import com.gzmtu.backend.util.ApiResponse;
import com.gzmtu.backend.dto.OrderCreationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;
import com.gzmtu.backend.repository.UserRepository;
import com.gzmtu.backend.entity.User;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ApiResponse<OrderDTO> createOrder(@RequestBody OrderCreationDTO orderCreationDTO) {
        return ApiResponse.success(orderService.createOrder(orderCreationDTO));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDTO> getOrderById(@PathVariable Integer id) {
        return ApiResponse.success(orderService.getOrderById(id));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderDTO>> getOrdersByUserId(@PathVariable Integer userId) {
        return ApiResponse.success(orderService.getOrdersByUserId(userId));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateOrderStatus(@PathVariable Integer id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return ApiResponse.success();
    }

    @GetMapping("/all")
    // @PreAuthorize("hasRole('ADMIN')") // 如需更严格可放开
    public ApiResponse<Page<OrderDTO>> getAllOrders(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String status) {
        Page<OrderDTO> allOrders = orderService.getAllOrders(page, size);
        // 过滤逻辑
        List<OrderDTO> filtered = allOrders.getContent();
        if (StringUtils.hasText(keyword)) {
            filtered = filtered.stream().filter(order ->
                (order.getOrderNumber() != null && order.getOrderNumber().contains(keyword)) ||
                (order.getUsername() != null && order.getUsername().contains(keyword))
            ).toList();
        }
        if (StringUtils.hasText(status)) {
            filtered = filtered.stream().filter(order -> status.equalsIgnoreCase(order.getStatus())).toList();
        }
        Page<OrderDTO> result = new PageImpl<>(filtered, allOrders.getPageable(), filtered.size());
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ApiResponse.success();
    }
} 