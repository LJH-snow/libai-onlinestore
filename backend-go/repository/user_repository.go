package repository

import (
	"backend-go/database"
	"backend-go/model"
)

// CreateUser creates a new user
func CreateUser(user *model.User) (*model.User, error) {
	err := database.DB.Create(user).Error
	return user, err
}

// CreateUserByAdmin creates a new user by admin
func CreateUserByAdmin(user *model.User) error {
	return database.DB.Create(user).Error
}

// FindUserByEmail finds a user by email
func FindUserByEmail(email string) (*model.User, error) {
	var user model.User
	err := database.DB.Where("email = ?", email).First(&user).Error
	if err != nil {
		return nil, err
	}
	return &user, nil
}

// FindUserByAccount finds a user by email, username, or phone
func FindUserByAccount(account string) (*model.User, error) {
	var user model.User
	err := database.DB.Where("email = ? OR username = ? OR phone = ?", account, account, account).First(&user).Error
	return &user, err
}

// ListAllUsers returns all users for testing
func ListAllUsers(page, size int, keyword, role string) ([]model.User, int64, error) {
	var users []model.User
    var total int64

    query := database.DB.Model(&model.User{})

    if keyword != "" {
        query = query.Where("username LIKE ? OR email LIKE ?", "%"+keyword+"%", "%"+keyword+"%")
    }

    if role != "" {
        query = query.Where("role = ?", role)
    }

    query.Count(&total)

    err := query.Offset(page * size).Limit(size).Find(&users).Error

    return users, total, err
}

// UpdateUser updates user information
func UpdateUser(user *model.User) error {
	return database.DB.Save(user).Error
}

// FindUserByUsername finds a user by username
func FindUserByUsername(username string) (*model.User, error) {
	var user model.User
	err := database.DB.Where("username = ?", username).First(&user).Error
	if err != nil {
		return nil, err
	}
	return &user, nil
}

// FindUserByID finds a user by ID
func FindUserByID(id uint) (*model.User, error) {
	var user model.User
	err := database.DB.First(&user, id).Error
	return &user, err
}



// DeleteUser deletes a user by ID
func DeleteUser(id uint) error {
	return database.DB.Delete(&model.User{}, id).Error
}

// UpdateUserStatus updates user status
func UpdateUserStatus(id uint, status int) error {
	return database.DB.Model(&model.User{}).Where("id = ?", id).Update("status", status).Error
}

// ListUsersWithPaginationAndFilter lists users with pagination and filtering
func ListUsersWithPaginationAndFilter(page, size int, keyword, role string) ([]model.User, int64, error) {
	var users []model.User
	var total int64
	
	query := database.DB.Model(&model.User{})
	
	// Apply filters
	if keyword != "" {
		query = query.Where("username LIKE ? OR email LIKE ?", "%"+keyword+"%", "%"+keyword+"%")
	}
	if role != "" {
		query = query.Where("role = ?", role)
	}
	
	// Get total count
	query.Count(&total)
	
	// Apply pagination
	offset := (page - 1) * size
	err := query.Offset(offset).Limit(size).Find(&users).Error
	
	return users, total, err
}


