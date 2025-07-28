package service

import (
	"errors"
	"backend-go/model"
	"backend-go/repository"
	"golang.org/x/crypto/bcrypt"
)

func GetAllUsers(page, size int, keyword, role string) ([]model.User, int64, error) {
	return repository.ListAllUsers(page, size, keyword, role)
}

func DeleteUser(id uint) error {
	return repository.DeleteUser(id)
}

// UpdateUserStatus updates the status of a user.
func UpdateUserStatus(id uint, status int) error {
	return repository.UpdateUserStatus(id, status)
}

// CreateUserByAdminInput defines the input for creating a user by admin.
type CreateUserByAdminInput struct {
	Username string `json:"username" binding:"required"`
	Email    string `json:"email" binding:"required,email"`
	Password string `json:"password" binding:"required,min=6"`
	Role     string `json:"role"`
	Status   int    `json:"status"`
}

// CreateUserByAdmin handles the business logic for creating a user by admin.
func CreateUserByAdmin(input CreateUserByAdminInput) (*model.User, error) {
	// Check if username or email already exists
	if user, _ := repository.FindUserByUsername(input.Username); user != nil {
		return nil, errors.New("username already exists")
	}
	if user, _ := repository.FindUserByEmail(input.Email); user != nil {
		return nil, errors.New("email already exists")
	}

	// Hash the password
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(input.Password), bcrypt.DefaultCost)
	if err != nil {
		return nil, err
	}

	// Create new user
	newUser := model.User{
		Username: input.Username,
		Email:    input.Email,
		Password: string(hashedPassword),
		Role:     input.Role,
		Status:   input.Status,
	}

	if err := repository.CreateUserByAdmin(&newUser); err != nil {
		return nil, err
	}

	return &newUser, nil
}

// ListUsers handles the business logic for listing users with pagination and filtering.
func ListUsers(page, size int, keyword, role string) ([]model.User, int64, error) {
	return repository.ListUsersWithPaginationAndFilter(page, size, keyword, role)
}


