package handler

import (
	"backend-go/service"
	"github.com/gin-gonic/gin"
	"net/http"
	"strconv"
)

// CreateBook handles book creation (admin only)
// POST /api/admin/books
func CreateBook(c *gin.Context) {
	var input service.CreateBookInput
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	book, err := service.CreateBook(input)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, book)
}

// GetBook handles getting a single book
// GET /api/books/:id
func GetBook(c *gin.Context) {
	id, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid book ID"})
		return
	}

	book, err := service.GetBookByID(uint(id))
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Book not found"})
		return
	}

	c.JSON(http.StatusOK, book)
}

// ListBooksForAdmin handles getting books for admin with pagination and filters
// GET /api/admin/books
func ListBooksForAdmin(c *gin.Context) {
	page, _ := strconv.Atoi(c.DefaultQuery("page", "0"))
	size, _ := strconv.Atoi(c.DefaultQuery("size", "10"))
	title := c.Query("title")
	categoryID := c.Query("categoryId")

	books, total, err := service.GetBooksForAdmin(page, size, title, categoryID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"books": books,
		"total": total,
		"page":  page,
		"size":  size,
	})
}

// ListBooks handles listing all books with pagination
// GET /api/books
func ListBooks(c *gin.Context) {
	page, _ := strconv.Atoi(c.DefaultQuery("page", "1"))
	size, _ := strconv.Atoi(c.DefaultQuery("size", "10"))

	books, total, err := service.GetBooksPaginated(page, size)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"books": books,
		"total": total,
		"page":  page,
		"size":  size,
	})
}

// UpdateBook handles book updates (admin only)
// PUT /api/admin/books/:id
func UpdateBook(c *gin.Context) {
	id, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid book ID"})
		return
	}

	var input service.UpdateBookInput
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	book, err := service.UpdateBook(uint(id), input)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, book)
}

// DeleteBook handles book deletion (admin only)
// DELETE /api/admin/books/:id
func DeleteBook(c *gin.Context) {
	id, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid book ID"})
		return
	}

	if err := service.DeleteBook(uint(id)); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Book deleted successfully"})
}

// SearchBooks handles book search
// GET /api/books/search
func SearchBooks(c *gin.Context) {
	keyword := c.Query("keyword")
	if keyword == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Keyword is required"})
		return
	}

	books, err := service.SearchBooks(keyword)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, books)
}

// UpdateBookStatus handles book status updates (admin only)
// PUT /api/admin/books/:id/status
func UpdateBookStatus(c *gin.Context) {
	id, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid book ID"})
		return
	}

	var input struct {
		Status string `json:"status" binding:"required"`
	}
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	book, err := service.UpdateBookStatus(uint(id), input.Status)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, book)
}



