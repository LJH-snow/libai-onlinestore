package handler

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"backend-go/service"
)

// GetUserIDFromContext retrieves the user ID from the Gin context.
func GetUserIDFromContext(c *gin.Context) (uint, bool) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not authenticated"})
		return 0, false
	}
	return userID.(uint), true
}

func ListAddresses(c *gin.Context) {
	userID, ok := GetUserIDFromContext(c)
	if !ok {
		return
	}
	addresses, err := service.GetAddressesByUserID(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to retrieve addresses"})
		return
	}
	c.JSON(http.StatusOK, addresses)
}

func CreateAddress(c *gin.Context) {
	userID, ok := GetUserIDFromContext(c)
	if !ok {
		return
	}
	var input service.AddressInput
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	address, err := service.CreateAddress(userID, input)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create address"})
		return
	}
	c.JSON(http.StatusOK, address)
}

func UpdateAddress(c *gin.Context) {
	_, ok := GetUserIDFromContext(c) // Just to ensure user is authenticated
	if !ok {
		return
	}
	addressID, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid address ID"})
		return
	}
	var input service.AddressInput
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	address, err := service.UpdateAddress(uint(addressID), input)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update address"})
		return
	}
	c.JSON(http.StatusOK, address)
}

func DeleteAddress(c *gin.Context) {
	userID, ok := GetUserIDFromContext(c)
	if !ok {
		return
	}
	addressID, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid address ID"})
		return
	}
	if err := service.DeleteAddress(uint(addressID), userID); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Address deleted successfully"})
}

func SetDefaultAddress(c *gin.Context) {
	userID, ok := GetUserIDFromContext(c)
	if !ok {
		return
	}
	addressID, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid address ID"})
		return
	}
	if err := service.SetDefaultAddress(userID, uint(addressID)); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to set default address"})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Default address set successfully"})
}

// GetUserAddresses handles getting addresses by user ID
// GET /api/addresses/user/:userId
func GetUserAddresses(c *gin.Context) {
	userID, err := strconv.ParseUint(c.Param("userId"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid user ID"})
		return
	}

	addresses, err := service.GetUserAddresses(uint(userID))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"data": addresses})
}

