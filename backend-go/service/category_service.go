package service

import (
	"backend-go/model"
	"backend-go/repository"
)

// CreateCategory creates a new category.
func CreateCategory(name, description string) (*model.Category, error) {
	category := &model.Category{
		Name:        name,
		Description: description,
	}
	return category, repository.CreateCategory(category)
}

// GetAllCategories returns all categories.
func GetAllCategories() ([]model.Category, error) {
	return repository.ListAllCategories()
}

// UpdateCategoryInput defines the input for updating a category.
type UpdateCategoryInput struct {
	Name        string `json:"name"`
	Description string `json:"description"`
}

func UpdateCategory(id uint, input UpdateCategoryInput) (*model.Category, error) {
	category, err := repository.FindCategoryByID(id)
	if err != nil {
		return nil, err
	}
	if input.Name != "" {
		category.Name = input.Name
	}
	if input.Description != "" {
		category.Description = input.Description
	}
	return category, repository.UpdateCategory(category)
}

func DeleteCategory(id uint) error {
	return repository.DeleteCategory(id)
}
