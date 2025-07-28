package service

import (
	"backend-go/model"
	"backend-go/repository"
	"errors"
	"fmt"
)

// OrderListResponse defines the structure for items in the order list response
type OrderListResponse struct {
	ID          uint    `json:"id"`
	OrderNumber string  `json:"orderNumber"`
	TotalPrice  float64 `json:"totalPrice"`
	Status      string  `json:"status"`
	CreatedAt   string  `json:"createdAt"`
	Address     string  `json:"address"` // Formatted address string
}

type CreateOrderInput struct {
	OrderNumber string            `json:"orderNumber"`
	UserID      uint              `json:"userId"`
	AddressID   uint              `json:"addressId"`
	TotalPrice  float64           `json:"totalPrice"`
	Status      string            `json:"status"`
	Items       []OrderItemInput  `json:"items" binding:"required"`
}

type OrderItemInput struct {
	BookID   uint    `json:"bookId" binding:"required"`
	Quantity int     `json:"quantity" binding:"required,min=1"`
	Price    float64 `json:"price"`
}

func CreateOrder(userID uint, input CreateOrderInput) (*model.Order, error) {
	if len(input.Items) == 0 {
		return nil, errors.New("order must contain at least one item")
	}

	var totalAmount float64
	var orderItems []model.OrderItem

	// 计算总金额并准备订单项
	for _, item := range input.Items {
		book, err := repository.FindBookByID(item.BookID)
		if err != nil {
			return nil, errors.New("book not found")
		}

		if book.Stock < item.Quantity {
			return nil, errors.New("insufficient stock")
		}

		orderItem := model.OrderItem{
			BookID:   item.BookID,
			Quantity: item.Quantity,
			Price:    item.Price, // 使用前端传来的价格
		}
		orderItems = append(orderItems, orderItem)
		totalAmount += item.Price * float64(item.Quantity)
	}

	// 创建订单 - 使用前端传来的数据
	order := &model.Order{
		UserID:      userID,
		AddressID:   input.AddressID, // 保存AddressID
		TotalAmount: input.TotalPrice, // 使用前端计算的总价
		Status:      input.Status,
		OrderNumber: input.OrderNumber,
	}

	// 在事务中创建订单和订单项
	err := repository.CreateOrderInTransaction(order, orderItems)
	if err != nil {
		return nil, err
	}

	return order, nil
}

func GetOrderByID(orderID, userID uint) (*model.Order, error) {
	return repository.FindOrderByIDAndUserID(orderID, userID)
}

func CancelOrder(orderID, userID uint) error {
    order, err := repository.FindOrderByIDAndUserID(orderID, userID)
    if err != nil {
        return errors.New("order not found or you don't have permission to cancel it")
    }

    if order.Status != "PENDING" {
        return errors.New("only pending orders can be cancelled")
    }

    return repository.UpdateOrderStatus(orderID, "CANCELLED")
}

func GetOrderItems(orderID uint) ([]model.OrderItem, error) {
	return repository.FindOrderItemsByOrderID(orderID)
}

func GetUserOrders(userID uint) ([]OrderListResponse, error) {
	orders, err := repository.FindOrdersByUserID(userID)
	if err != nil {
		return nil, err
	}

	var responseOrders []OrderListResponse
	for _, order := range orders {
		var addressStr string
		if order.Address.ID != 0 {
			addressStr = fmt.Sprintf("%s %s %s %s (收件人: %s, 电话: %s)",
				order.Address.Province, order.Address.City, order.Address.District, order.Address.DetailAddress,
				order.Address.ReceiverName, order.Address.Phone)
		} else {
			addressStr = "地址信息不存在"
		}

		responseOrders = append(responseOrders, OrderListResponse{
			ID:          order.ID,
			OrderNumber: order.OrderNumber,
			TotalPrice:  order.TotalAmount,
			Status:      order.Status,
			CreatedAt:   order.CreatedAt.Format("2006-01-02 15:04:05"),
			Address:     addressStr,
		})
	}
	return responseOrders, nil
}

func GetAllOrders(page, size int, keyword, status string) ([]model.Order, int64, error) {
	return repository.FindAllOrdersPaginated(page, size, keyword, status)
}

func UpdateOrderStatus(orderID uint, status string) error {
	_, err := repository.FindOrderByID(orderID)
	if err != nil {
		return errors.New("order not found")
	}
	
	return repository.UpdateOrderStatus(orderID, status)
}

func DeleteOrder(orderID uint) error {
	return repository.DeleteOrder(orderID)
}

func DeleteOrderForUser(orderID uint, userID uint) error {
    order, err := repository.FindOrderByIDAndUserID(orderID, userID)
    if err != nil {
        return errors.New("order not found or you don't have permission to delete it")
    }

    if order.Status != "CANCELLED" {
        return errors.New("only cancelled orders can be deleted")
    }

    return repository.DeleteOrder(orderID)
}




