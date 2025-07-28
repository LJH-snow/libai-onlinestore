package model

import "time"

// CartItem corresponds to the `cart_item` table.
type CartItem struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	CartID    uint      `gorm:"column:cart_id;not null" json:"cartId"`
	BookID    uint      `gorm:"column:book_id;not null" json:"bookId"`
	Quantity  int       `gorm:"not null;default:1" json:"quantity"`
	Book      Book      `gorm:"foreignKey:BookID" json:"book"`
	CreatedAt time.Time `gorm:"column:created_at" json:"createdAt"`
	UpdatedAt time.Time `gorm:"column:updated_at" json:"updatedAt"`
}

func (CartItem) TableName() string {
	return "cart_item"
}
