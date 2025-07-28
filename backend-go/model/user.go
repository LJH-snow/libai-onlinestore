package model

import "time"

// User corresponds to the `user` table.
type User struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	Username  string    `gorm:"type:varchar(50);unique;not null" json:"username"`
	Password  string    `gorm:"type:varchar(255);not null" json:"-"`
	Email     string    `gorm:"type:varchar(255);unique;not null" json:"email"`
	Phone     string    `gorm:"type:varchar(20);unique" json:"phone,omitempty"`
	Role      string    `gorm:"type:varchar(50);default:'user'" json:"role"`
	Status    int       `gorm:"default:1" json:"status"`
	CreatedAt time.Time `gorm:"column:created_at" json:"createdAt"`
	UpdatedAt time.Time `gorm:"column:updated_at" json:"updatedAt"`
}

func (User) TableName() string {
	return "user"
}
