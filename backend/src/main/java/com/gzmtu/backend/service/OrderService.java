package com.gzmtu.backend.service;

import com.gzmtu.backend.dto.OrderDTO;
import com.gzmtu.backend.dto.OrderCreationDTO;
import com.gzmtu.backend.entity.Order;
import java.util.List;
import org.springframework.data.domain.Page;

public interface OrderService {
    OrderDTO createOrder(OrderCreationDTO orderCreationDTO);
    OrderDTO getOrderById(Integer id);
    List<OrderDTO> getOrdersByUserId(Integer userId);
    void updateOrderStatus(Integer id, String status);
    Page<OrderDTO> getAllOrders(int page, int size);
    void deleteOrder(Integer id);
} 