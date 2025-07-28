package model

import "time"

// Book corresponds to the `book` table.
type Book struct {
	ID          uint      `gorm:"primaryKey" json:"id"`
	Title       string    `gorm:"type:varchar(200);not null" json:"title"`
	Author      string    `gorm:"type:varchar(100);not null" json:"author"`
	Publisher   string    `gorm:"type:varchar(100)" json:"publisher,omitempty"`
	Isbn        string    `gorm:"type:varchar(20);unique" json:"isbn,omitempty"`
	Price       float64   `gorm:"type:decimal(10,2);not null" json:"price"`
	Stock       int       `gorm:"default:0" json:"stock"`
	Description string    `gorm:"type:text" json:"description,omitempty"`
	ImageURL    string    `gorm:"column:image_url;type:varchar(500)" json:"cover,omitempty"`
	Status      string    `gorm:"type:varchar(20);default:'on'" json:"status,omitempty"`
	CategoryID  uint      `gorm:"column:category_id" json:"categoryId"`
	CreatedAt   time.Time `gorm:"column:created_at" json:"createdAt"`
	UpdatedAt   time.Time `gorm:"column:updated_at" json:"updatedAt"`
}

func (Book) TableName() string {
	return "book"
}
