package service

import (
	"errors"
	"backend-go/model"
	"backend-go/repository"
	"backend-go/utils"
    "golang.org/x/crypto/bcrypt"
)

type RegisterInput struct {
	Username string `json:"username" binding:"required"`
	Email    string `json:"email" binding:"required,email"`
	Phone    string `json:"phone"`
	Password string `json:"password" binding:"required,min=6"`
}

type LoginInput struct {
	Account  string `json:"account" binding:"required"`  // 支持邮箱、用户名、手机号
	Password string `json:"password" binding:"required"`
}

func RegisterUser(input RegisterInput) (*model.User, error) {
	// 检查用户是否已存在
	existingUser, _ := repository.FindUserByEmail(input.Email)
	if existingUser != nil {
		return nil, errors.New("user already exists")
	}

    // 加密密码
    hashedPassword, err := bcrypt.GenerateFromPassword([]byte(input.Password), bcrypt.DefaultCost)
    if err != nil {
        return nil, err
    }

	// 创建用户
	user := &model.User{
		Username: input.Username,
		Email:    input.Email,
		Phone:    input.Phone,
		Password: string(hashedPassword), // 存储加密后的密码
		Role:     "USER",
		Status:   1, // 使用 int 类型
	}

	return repository.CreateUser(user)
}

func LoginUser(input LoginInput) (*model.User, string, error) {
	// 查找用户 - 支持邮箱、用户名、手机号
	user, err := repository.FindUserByAccount(input.Account)
	if err != nil {
		return nil, "", errors.New("invalid credentials")
	}

	// 验证密码 - 使用 bcrypt 进行比较
    err = bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(input.Password))
    if err != nil {
        return nil, "", errors.New("invalid credentials")
    }

	// 生成JWT token
	token, err := utils.GenerateJWT(user.ID, user.Email, user.Role)
	if err != nil {
		return nil, "", err
	}

	return user, token, nil
}

type UpdateUserInput struct {
	Email string `json:"email" binding:"required,email"`
	Phone string `json:"phone"`
}

// UpdateUserProfile updates user profile information
func UpdateUserProfile(userID uint, input UpdateUserInput) (*model.User, error) {
	// 1. Find the user first
	user, err := repository.FindUserByID(userID)
	if err != nil {
		return nil, err // Return error if user not found
	}

	// 2. Update the fields
	user.Email = input.Email
	user.Phone = input.Phone

	// 3. Save the updated user object
	if err := repository.UpdateUser(user); err != nil {
		return nil, err
	}

	return user, nil
}

func GetUserProfile(userID uint) (*model.User, error) {
	return repository.FindUserByID(userID)
}

func GetUserByID(userID uint) (*model.User, error) {
	return repository.FindUserByID(userID)
}



