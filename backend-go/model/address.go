package model

import "time"

// Address corresponds to the `address` table.
type Address struct {
	ID            uint      `gorm:"primaryKey" json:"id"`
	UserID        uint      `gorm:"column:user_id;not null" json:"userId"`
	ReceiverName  string    `gorm:"column:receiver_name;type:varchar(50);not null" json:"receiverName"`
	Phone         string    `gorm:"type:varchar(20);not null" json:"phone"`
	Province      string    `gorm:"type:varchar(50);not null" json:"province"`
	City          string    `gorm:"type:varchar(50);not null" json:"city"`
	District      string    `gorm:"type:varchar(50);not null" json:"district"`
	DetailAddress string    `gorm:"column:detail_address;type:text;not null" json:"detailAddress"`
	IsDefault     bool      `gorm:"column:is_default;default:false" json:"isDefault"`
	CreatedAt     time.Time `gorm:"column:created_at" json:"createdAt"`
}

func (Address) TableName() string {
	return "address"
}
