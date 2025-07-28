package model

import "time"

// OrderItem corresponds to the `order_item` table.
type OrderItem struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	OrderID   uint      `gorm:"column:order_id;not null" json:"orderId"`
	BookID    uint      `gorm:"column:book_id;not null" json:"bookId"`
	Quantity  int       `gorm:"not null" json:"quantity"`
	Price     float64   `gorm:"type:decimal(10,2);not null" json:"price"`
	Book      Book      `gorm:"foreignKey:BookID" json:"book"`
	CreatedAt time.Time `gorm:"column:created_at" json:"createdAt"`
}

func (OrderItem) TableName() string {
	return "order_item"
}
