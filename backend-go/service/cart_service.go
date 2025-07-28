package service

import (
	"errors"
	"backend-go/database"
	"backend-go/model"
	"backend-go/repository"
)

// CartItemResponse defines the structure for a single item in the cart response.
type CartItemResponse struct {
	ID       uint    `json:"id"`
	BookID   uint    `json:"bookId"`
	Quantity int     `json:"quantity"`
	Title    string  `json:"title"`
	Cover    string  `json:"cover"`
	Price    float64 `json:"price"`
}

// CartResponse defines the structure for the cart API response.
type CartResponse struct {
	ID    uint               `json:"id"`
	Items []CartItemResponse `json:"items"`
}

// GetCartForUser retrieves the user's cart and its items.
func GetCartForUser(userID uint) (*CartResponse, error) {
	cart, err := repository.FindOrCreateCartByUserID(userID)
	if err != nil {
		return nil, err
	}

	items, err := repository.GetCartItemsByCartID(cart.ID)
	if err != nil {
		return nil, err
	}

	var responseItems []CartItemResponse
	for _, item := range items {
		responseItems = append(responseItems, CartItemResponse{
			ID:       item.ID,
			BookID:   item.BookID,
			Quantity: item.Quantity,
			Title:    item.Book.Title,
			Cover:    item.Book.ImageURL,
			Price:    item.Book.Price,
		})
	}

	return &CartResponse{ID: cart.ID, Items: responseItems}, nil
}

// AddToCart adds a book to the user's cart.
func AddToCart(userID, bookID uint, quantity int) (*model.CartItem, error) {
	cart, err := repository.FindOrCreateCartByUserID(userID)
	if err != nil {
		return nil, err
	}

	// Check if the item already exists in the cart
	existingItem, err := repository.FindCartItemByCartIDAndBookID(cart.ID, bookID)
	if err != nil {
		return nil, err
	}

	if existingItem != nil {
		// Item exists, update quantity
		existingItem.Quantity += quantity
		return existingItem, repository.UpdateCartItem(existingItem)
	} else {
		// Item does not exist, create a new one
		newItem := &model.CartItem{
			CartID:   cart.ID,
			BookID:   bookID,
			Quantity: quantity,
		}
		return newItem, repository.CreateCartItem(newItem)
	}
}

// UpdateCartItemQuantity updates the quantity of an item in the cart.
func UpdateCartItemQuantity(userID, itemID uint, quantity int) (*model.CartItem, error) {
	if quantity <= 0 {
		return nil, errors.New("quantity must be positive")
	}

	cart, err := repository.FindOrCreateCartByUserID(userID)
	if err != nil {
		return nil, err
	}

	// Find the item to ensure it belongs to the user's cart
	var item model.CartItem
	if err := database.DB.Where("id = ? AND cart_id = ?", itemID, cart.ID).First(&item).Error; err != nil {
		return nil, errors.New("cart item not found")
	}

	item.Quantity = quantity
	return &item, repository.UpdateCartItem(&item)
}

// RemoveFromCart removes an item from the user's cart.
func RemoveFromCart(userID, itemID uint) error {
	cart, err := repository.FindOrCreateCartByUserID(userID)
	if err != nil {
		return err
	}

	// Verify the item belongs to the user's cart before deleting
	var item model.CartItem
	if err := database.DB.Where("id = ? AND cart_id = ?", itemID, cart.ID).First(&item).Error; err != nil {
		return errors.New("cart item not found or does not belong to user")
	}

	return repository.DeleteCartItem(itemID)
}

// ClearCart removes all items from a user's cart.
func ClearCart(userID uint) error {
	cart, err := repository.FindOrCreateCartByUserID(userID)
	if err != nil {
		return err
	}
	return repository.ClearCart(cart.ID)
}
