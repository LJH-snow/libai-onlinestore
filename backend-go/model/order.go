package model

import "time"

// Order corresponds to the `order` table.
type Order struct {
	ID          uint      `gorm:"primaryKey" json:"id"`
	UserID      uint      `gorm:"column:user_id;not null" json:"userId"`
    User        User      `gorm:"foreignKey:UserID" json:"user"`
	OrderNumber string    `gorm:"column:order_number;type:varchar(50);unique;not null" json:"orderNumber"`
	TotalAmount float64   `gorm:"column:total_amount;type:decimal(10,2);not null" json:"totalPrice"`
	Status      string    `gorm:"type:varchar(20);default:'PENDING'" json:"status"`
	AddressID   uint      `gorm:"column:address_id;not null" json:"addressId"`
	Address     Address   `gorm:"foreignKey:AddressID" json:"address"`
	Items       []OrderItem `gorm:"foreignKey:OrderID" json:"items"`
	CreatedAt   time.Time `gorm:"column:created_at" json:"createdAt"`
	UpdatedAt   time.Time `gorm:"column:updated_at" json:"updatedAt"`
}

func (Order) TableName() string {
	return "`order`"
}
