package repository

import (
	"backend-go/database"
	"backend-go/model"
)

func CreateCategory(category *model.Category) error {
	return database.DB.Create(category).Error
}

func FindCategoryByID(id uint) (*model.Category, error) {
	var category model.Category
	if err := database.DB.First(&category, id).Error; err != nil {
		return nil, err
	}
	return &category, nil
}

func ListAllCategories() ([]model.Category, error) {
	var categories []model.Category
	if err := database.DB.Find(&categories).Error; err != nil {
		return nil, err
	}
	return categories, nil
}

func UpdateCategory(category *model.Category) error {
	return database.DB.Save(category).Error
}

func DeleteCategory(id uint) error {
	return database.DB.Delete(&model.Category{}, id).Error
}
