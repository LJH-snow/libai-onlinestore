package repository

import (
	"backend-go/database"
	"backend-go/model"
)

// CreateBook creates a new book
func CreateBook(book *model.Book) (*model.Book, error) {
	err := database.DB.Create(book).Error
	return book, err
}

// FindBookByID finds a book by ID
func FindBookByID(id uint) (*model.Book, error) {
	var book model.Book
	err := database.DB.First(&book, id).Error
	return &book, err
}

// FindAllBooks retrieves all books
func FindAllBooks() ([]model.Book, error) {
	var books []model.Book
	err := database.DB.Find(&books).Error
	return books, err
}

// FindBooksPaginated finds books with pagination, only for active books
func FindBooksPaginated(offset, size int) ([]model.Book, error) {
	var books []model.Book
	err := database.DB.Where("status = ?", "on").Offset(offset).Limit(size).Find(&books).Error
	return books, err
}

// CountAllBooks counts total books
func CountAllBooks() (int64, error) {
	var count int64
	err := database.DB.Model(&model.Book{}).Count(&count).Error
	return count, err
}

// UpdateBook updates book information
func UpdateBook(book *model.Book) error {
	return database.DB.Save(book).Error
}

// DeleteBookByID deletes a book by ID
func DeleteBookByID(bookID uint) error {
	return database.DB.Delete(&model.Book{}, bookID).Error
}

// SearchBooks searches books by title or author
func SearchBooks(keyword string) ([]model.Book, error) {
	var books []model.Book
	err := database.DB.Where("title LIKE ? OR author LIKE ?", "%"+keyword+"%", "%"+keyword+"%").Find(&books).Error
	return books, err
}

// UpdateBookStatus updates book status
func UpdateBookStatus(bookID uint, status string) error {
	return database.DB.Model(&model.Book{}).Where("id = ?", bookID).Update("status", status).Error
}

// FindBooksForAdmin finds books for admin with pagination and filters
func FindBooksForAdmin(page, size int, title, categoryID string) ([]model.Book, int64, error) {
	var books []model.Book
	var total int64

	query := database.DB.Model(&model.Book{})

	// Apply filters
	if title != "" {
		query = query.Where("title LIKE ? OR author LIKE ?", "%"+title+"%", "%"+title+"%")
	}
	if categoryID != "" {
		query = query.Where("category_id = ?", categoryID)
	}

	// Get total count
	err := query.Count(&total).Error
	if err != nil {
		return nil, 0, err
	}

	// Apply pagination
	offset := page * size
	err = query.Offset(offset).Limit(size).Find(&books).Error

	return books, total, err
}

