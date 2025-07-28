package repository

import (
	"backend-go/database"
	"backend-go/model"
)

// CreateOrder creates a new order
func CreateOrder(order *model.Order) (*model.Order, error) {
	err := database.DB.Create(order).Error
	return order, err
}

func CreateOrderInTransaction(order *model.Order, orderItems []model.OrderItem) error {
	tx := database.DB.Begin()
	
	// 创建订单
	if err := tx.Create(order).Error; err != nil {
		tx.Rollback()
		return err
	}
	
	// 为订单项设置订单ID
	for i := range orderItems {
		orderItems[i].OrderID = order.ID
	}
	
	// 创建订单项
	if err := tx.Create(&orderItems).Error; err != nil {
		tx.Rollback()
		return err
	}
	
	return tx.Commit().Error
}

// FindOrderByID finds an order by ID
func FindOrderByID(orderID uint) (*model.Order, error) {
	var order model.Order
	err := database.DB.First(&order, orderID).Error
	return &order, err
}

func FindOrderByIDAndUserID(orderID, userID uint) (*model.Order, error) {
	var order model.Order
	err := database.DB.Preload("Address").Preload("Items.Book").Where("id = ? AND user_id = ?", orderID, userID).First(&order).Error
	return &order, err
}

// FindOrdersByUserID finds orders by user ID
func FindOrdersByUserID(userID uint) ([]model.Order, error) {
	var orders []model.Order
	err := database.DB.Preload("Address").Where("user_id = ?", userID).Find(&orders).Error
	return orders, err
}

func FindAllOrders() ([]model.Order, error) {
	var orders []model.Order
	err := database.DB.Preload("Address").Find(&orders).Error
	return orders, err
}

func FindAllOrdersPaginated(page, size int, keyword, status string) ([]model.Order, int64, error) {
    var orders []model.Order
    var total int64

    query := database.DB.Model(&model.Order{}).Preload("User").Preload("Address").Preload("Items.Book")

    if keyword != "" {
        query = query.Joins("join user on user.id = `order`.user_id").
            Where("`order`.order_number LIKE ? OR user.username LIKE ?", "%"+keyword+"%", "%"+keyword+"%")
    }

    if status != "" {
        query = query.Where("`order`.status = ?", status)
    }

    query.Count(&total)

    err := query.Offset(page * size).Limit(size).Find(&orders).Error

    return orders, total, err
}

func FindOrderItemsByOrderID(orderID uint) ([]model.OrderItem, error) {
	var orderItems []model.OrderItem
	err := database.DB.Where("order_id = ?", orderID).Find(&orderItems).Error
	return orderItems, err
}

// UpdateOrderStatus updates order status
func UpdateOrderStatus(orderID uint, status string) error {
	return database.DB.Model(&model.Order{}).Where("id = ?", orderID).Update("status", status).Error
}

// DeleteOrder deletes an order
func DeleteOrder(orderID uint) error {
    tx := database.DB.Begin()

    // 首先删除订单项
    if err := tx.Where("order_id = ?", orderID).Delete(&model.OrderItem{}).Error; err != nil {
        tx.Rollback()
        return err
    }

    // 然后删除订单本身
    if err := tx.Delete(&model.Order{}, orderID).Error; err != nil {
        tx.Rollback()
        return err
    }

    return tx.Commit().Error
}


