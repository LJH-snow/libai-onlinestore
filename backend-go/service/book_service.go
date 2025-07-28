package service

import (
	"backend-go/model"
	"backend-go/repository"
	"errors"
)

// CreateBookInput defines the input for creating a book
type CreateBookInput struct {
	Title       string  `json:"title" binding:"required"`
	Author      string  `json:"author" binding:"required"`
	Publisher   string  `json:"publisher"`
    Isbn        string  `json:"isbn" binding:"required"`
	Price       float64 `json:"price" binding:"required,min=0"`
	Stock       int     `json:"stock" binding:"required,min=0"`
	Description string  `json:"description"`
	Cover       string  `json:"cover"`
	CategoryID  uint    `json:"categoryId"`
}

// UpdateBookInput defines the input for updating a book
type UpdateBookInput struct {
	Title       string  `json:"title"`
	Author      string  `json:"author"`
	Publisher   string  `json:"publisher"`
	Price       float64 `json:"price" binding:"min=0"`
	Stock       int     `json:"stock" binding:"min=0"`
	Description string  `json:"description"`
	Cover       string  `json:"cover"`
	CategoryID  uint    `json:"categoryId"`
}

// CreateBook creates a new book
func CreateBook(input CreateBookInput) (*model.Book, error) {
    if input.Isbn == "" {
        return nil, errors.New("ISBN cannot be empty")
    }
	book := &model.Book{
		Title:       input.Title,
		Author:      input.Author,
		Publisher:   input.Publisher,
        Isbn:        input.Isbn,
		Price:       input.Price,
		Stock:       input.Stock,
		Description: input.Description,
		ImageURL:    input.Cover,
		CategoryID:  input.CategoryID,
		Status:      "on", // 默认状态为上架
	}

	return repository.CreateBook(book)
}

// GetBookByID retrieves a book by ID
func GetBookByID(bookID uint) (*model.Book, error) {
	return repository.FindBookByID(bookID)
}

// GetAllBooks retrieves all books
func GetAllBooks() ([]model.Book, error) {
	return repository.FindAllBooks()
}

// GetBooksPaginated retrieves books with pagination
func GetBooksPaginated(page, size int) ([]model.Book, int64, error) {
	offset := (page - 1) * size
	books, err := repository.FindBooksPaginated(offset, size)
	if err != nil {
		return nil, 0, err
	}

	total, err := repository.CountAllBooks()
	if err != nil {
		return nil, 0, err
	}

	return books, total, nil
}

// UpdateBook updates book information
func UpdateBook(bookID uint, input UpdateBookInput) (*model.Book, error) {
	book, err := repository.FindBookByID(bookID)
	if err != nil {
		return nil, err
	}

	if input.Title != "" {
		book.Title = input.Title
	}
	if input.Author != "" {
		book.Author = input.Author
	}
	if input.Publisher != "" {
		book.Publisher = input.Publisher
	}
	if input.Price > 0 {
		book.Price = input.Price
	}
	if input.Stock >= 0 {
		book.Stock = input.Stock
	}
	if input.Description != "" {
		book.Description = input.Description
	}
	if input.Cover != "" {
		book.ImageURL = input.Cover
	}
	if input.CategoryID > 0 {
		book.CategoryID = input.CategoryID
	}

	err = repository.UpdateBook(book)
	return book, err
}

// DeleteBook deletes a book
func DeleteBook(bookID uint) error {
	return repository.DeleteBookByID(bookID)
}

// SearchBooks searches books by keyword
func SearchBooks(keyword string) ([]model.Book, error) {
	return repository.SearchBooks(keyword)
}

// UpdateBookStatus updates book status
func UpdateBookStatus(bookID uint, status string) (*model.Book, error) {
	_, err := repository.FindBookByID(bookID)
	if err != nil {
		return nil, errors.New("book not found")
	}
	
	err = repository.UpdateBookStatus(bookID, status)
	if err != nil {
		return nil, err
	}
	
	// Return updated book
	return repository.FindBookByID(bookID)
}

// GetAllBooksForAdmin gets all books for admin management
func GetAllBooksForAdmin() ([]model.Book, error) {
	return repository.FindAllBooks()
}

// GetBooksForAdmin gets books for admin with pagination and filters
func GetBooksForAdmin(page, size int, title, categoryID string) ([]model.Book, int64, error) {
	return repository.FindBooksForAdmin(page, size, title, categoryID)
}


