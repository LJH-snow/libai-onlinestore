package handler

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"backend-go/service"
)

func GetCart(c *gin.Context) {
	userID, ok := GetUserIDFromContext(c)
	if !ok {
		return
	}
	cart, err := service.GetCartForUser(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to retrieve cart"})
		return
	}
	c.JSON(http.StatusOK, cart)
}

type AddToCartInput struct {
	BookID   uint `json:"bookId" binding:"required"`
	Quantity int  `json:"quantity" binding:"required,min=1"`
}

func AddToCart(c *gin.Context) {
	userID, ok := GetUserIDFromContext(c)
	if !ok {
		return
	}
	var input AddToCartInput
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	item, err := service.AddToCart(userID, input.BookID, input.Quantity)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to add item to cart"})
		return
	}
	c.JSON(http.StatusOK, item)
}

type UpdateCartItemInput struct {
	Quantity int `json:"quantity" binding:"required,min=1"`
}

func UpdateCartItemQuantity(c *gin.Context) {
	userID, ok := GetUserIDFromContext(c)
	if !ok {
		return
	}
	itemID, err := strconv.ParseUint(c.Param("itemId"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid item ID"})
		return
	}
	var input UpdateCartItemInput
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	item, err := service.UpdateCartItemQuantity(userID, uint(itemID), input.Quantity)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, item)
}

func RemoveFromCart(c *gin.Context) {
	userID, ok := GetUserIDFromContext(c)
	if !ok {
		return
	}
	itemID, err := strconv.ParseUint(c.Param("itemId"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid item ID"})
		return
	}
	if err := service.RemoveFromCart(userID, uint(itemID)); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Item removed from cart successfully"})
}

func ClearCart(c *gin.Context) {
	userID, ok := GetUserIDFromContext(c)
	if !ok {
		return
	}
	if err := service.ClearCart(userID); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to clear cart"})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Cart cleared successfully"})
}
