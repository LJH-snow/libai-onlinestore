package com.gzmtu.backend.service.impl;

import com.gzmtu.backend.dto.OrderDTO;
import com.gzmtu.backend.entity.Order;
import com.gzmtu.backend.repository.OrderRepository;
import com.gzmtu.backend.service.OrderService;
import com.gzmtu.backend.repository.AddressRepository;
import com.gzmtu.backend.entity.Address;
import com.gzmtu.backend.repository.OrderItemRepository;
import com.gzmtu.backend.dto.OrderItemDTO;
import com.gzmtu.backend.repository.BookRepository;
import com.gzmtu.backend.entity.Book;
import com.gzmtu.backend.dto.OrderCreationDTO;
import com.gzmtu.backend.entity.OrderItem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.gzmtu.backend.repository.UserRepository;
import com.gzmtu.backend.entity.User;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderCreationDTO orderCreationDTO) {
        Order order = new Order();
        order.setUserId(orderCreationDTO.getUserId());
        order.setAddressId(orderCreationDTO.getAddressId());
        order.setOrderNumber(orderCreationDTO.getOrderNumber());
        order.setTotalPrice(orderCreationDTO.getTotalPrice());
        order.setStatus(orderCreationDTO.getStatus());
        order.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        
        Order savedOrder = orderRepository.save(order);
        Integer orderId = savedOrder.getId();

        if (orderCreationDTO.getItems() != null && !orderCreationDTO.getItems().isEmpty()) {
            for (OrderCreationDTO.OrderItemCreationDTO itemDto : orderCreationDTO.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                orderItem.setBookId(itemDto.getBookId());
                orderItem.setQuantity(itemDto.getQuantity());
                orderItem.setPrice(itemDto.getPrice());
                orderItemRepository.save(orderItem);
            }
        }
        
        return getOrderById(orderId);
    }

    @Override
    public OrderDTO getOrderById(Integer id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return null;
        }
        Order order = orderOpt.get();

        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);

        if (order.getAddressId() != null) {
            addressRepository.findById(order.getAddressId())
                .ifPresent(address -> {
                    dto.setFullAddress(address.getAddress());
                    if (address.getUserId() != null) {
                        userRepository.findById(address.getUserId()).ifPresent(user -> {
                            dto.setConsignee(user.getRealName());
                            dto.setPhone(user.getPhone());
                        });
                    }
                });
        }

        if (order.getUserId() != null) {
            userRepository.findById(order.getUserId()).ifPresent(user -> dto.setUsername(user.getUsername()));
        }

        List<OrderItemDTO> itemDTOs = orderItemRepository.findByOrderId(id).stream()
            .map(item -> {
                OrderItemDTO itemDto = new OrderItemDTO();
                BeanUtils.copyProperties(item, itemDto);
                
                if (item.getBookId() != null) {
                    bookRepository.findById(item.getBookId()).ifPresent(book -> {
                        itemDto.setBookTitle(book.getTitle());
                        itemDto.setBookCover(book.getCover());
                    });
                }
                return itemDto;
            }).collect(Collectors.toList());

        dto.setItems(itemDTOs);

        return dto;
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(Integer userId) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getUserId().equals(userId))
                .map(order -> {
                    OrderDTO dto = new OrderDTO();
                    BeanUtils.copyProperties(order, dto);
                    
                    if (order.getAddressId() != null) {
                        Optional<Address> addressOpt = addressRepository.findById(order.getAddressId());
                        addressOpt.ifPresent(address -> dto.setAddress(address.getAddress()));
                    }
                    
                    if (order.getUserId() != null) {
                        userRepository.findById(order.getUserId()).ifPresent(user -> dto.setUsername(user.getUsername()));
                    }
                    
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(Integer id, String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
    }

    @Override
    public Page<OrderDTO> getAllOrders(int page, int size) {
        Page<Order> orderPage = orderRepository.findAll(PageRequest.of(page, size));
        List<OrderDTO> dtoList = orderPage.getContent().stream().map(order -> {
            OrderDTO dto = new OrderDTO();
            BeanUtils.copyProperties(order, dto);
            if (order.getAddressId() != null) {
                addressRepository.findById(order.getAddressId())
                    .ifPresent(address -> {
                        dto.setFullAddress(address.getAddress());
                        if (address.getUserId() != null) {
                            userRepository.findById(address.getUserId()).ifPresent(user -> {
                                dto.setConsignee(user.getRealName());
                                dto.setPhone(user.getPhone());
                            });
                        }
                    });
            }
            if (order.getUserId() != null) {
                userRepository.findById(order.getUserId()).ifPresent(user -> dto.setUsername(user.getUsername()));
            }
            List<OrderItemDTO> itemDTOs = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(item -> {
                    OrderItemDTO itemDto = new OrderItemDTO();
                    BeanUtils.copyProperties(item, itemDto);
                    if (item.getBookId() != null) {
                        bookRepository.findById(item.getBookId()).ifPresent(book -> {
                            itemDto.setBookTitle(book.getTitle());
                            itemDto.setBookCover(book.getCover());
                        });
                    }
                    return itemDto;
                }).collect(Collectors.toList());
            dto.setItems(itemDTOs);
            return dto;
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, orderPage.getPageable(), orderPage.getTotalElements());
    }

    @Override
    public void deleteOrder(Integer id) {
        // 先删除订单项
        orderItemRepository.findByOrderId(id).forEach(item -> {
            orderItemRepository.deleteById(item.getId());
        });
        // 再删除订单
        orderRepository.deleteById(id);
    }
} 