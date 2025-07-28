package model

import "time"

// Category corresponds to the `category` table.
type Category struct {
	ID          uint      `gorm:"primaryKey" json:"id"`
	Name        string    `gorm:"type:varchar(100);not null" json:"name"`
	Description string    `gorm:"type:text" json:"description,omitempty"`
	CreatedAt   time.Time `gorm:"column:created_at" json:"createdAt"`
}

func (Category) TableName() string {
	return "category"
}
