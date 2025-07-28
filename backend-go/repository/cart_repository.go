package repository

import (
	"backend-go/database"
	"backend-go/model"
	"gorm.io/gorm"
)

// FindOrCreateCartByUserID finds a cart for a user, or creates it if it doesn't exist.
func FindOrCreateCartByUserID(userID uint) (*model.Cart, error) {
	var cart model.Cart
	if err := database.DB.Where("user_id = ?", userID).FirstOrCreate(&cart, model.Cart{UserID: userID}).Error; err != nil {
		return nil, err
	}
	return &cart, nil
}

// GetCartItemsByCartID retrieves all items for a given cart ID.
func GetCartItemsByCartID(cartID uint) ([]model.CartItem, error) {
	var items []model.CartItem
	if err := database.DB.Preload("Book").Where("cart_id = ?", cartID).Find(&items).Error; err != nil {
		return nil, err
	}
	return items, nil
}

// FindCartItemByCartIDAndBookID finds a specific item in a cart.
func FindCartItemByCartIDAndBookID(cartID, bookID uint) (*model.CartItem, error) {
	var item model.CartItem
	err := database.DB.Where("cart_id = ? AND book_id = ?", cartID, bookID).First(&item).Error
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, nil // Not an error, just means item doesn't exist
		}
		return nil, err
	}
	return &item, nil
}

// CreateCartItem adds a new item to the cart.
func CreateCartItem(item *model.CartItem) error {
	return database.DB.Create(item).Error
}

// UpdateCartItem updates an existing cart item.
func UpdateCartItem(item *model.CartItem) error {
	return database.DB.Save(item).Error
}

// DeleteCartItem removes an item from the cart.
func DeleteCartItem(itemID uint) error {
	return database.DB.Delete(&model.CartItem{}, itemID).Error
}

// ClearCart deletes all items associated with a cart ID.
func ClearCart(cartID uint) error {
	return database.DB.Where("cart_id = ?", cartID).Delete(&model.CartItem{}).Error
}
