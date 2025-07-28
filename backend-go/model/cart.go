package model

import "time"

// Cart corresponds to the `cart` table.
type Cart struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	UserID    uint      `gorm:"column:user_id;not null" json:"userId"`
	CreatedAt time.Time `gorm:"column:created_at" json:"createdAt"`
}

func (Cart) TableName() string {
	return "cart"
}
