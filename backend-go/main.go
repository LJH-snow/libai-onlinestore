package main

import (
	"fmt"
	"backend-go/config"
	"backend-go/database"
	"backend-go/router"
    "backend-go/model"
    "golang.org/x/crypto/bcrypt"
)

// migratePasswords checks for plaintext passwords and hashes them.
func migratePasswords() {
    fmt.Println("Checking for password migrations...")
    var users []model.User
    // Find users whose password does not start with the bcrypt prefix.
    if err := database.DB.Where("password NOT LIKE ?", "$2a$%").Find(&users).Error; err != nil {
        fmt.Printf("Error finding users for password migration: %v\n", err)
        return
    }

    if len(users) == 0 {
        fmt.Println("No plaintext passwords found. Migration not needed.")
        return
    }

    for _, user := range users {
        fmt.Printf("Migrating password for user: %s\n", user.Username)
        hashedPassword, err := bcrypt.GenerateFromPassword([]byte(user.Password), bcrypt.DefaultCost)
        if err != nil {
            fmt.Printf("Error hashing password for user %s: %v\n", user.Username, err)
            continue // Skip to the next user
        }
        user.Password = string(hashedPassword)
        if err := database.DB.Save(&user).Error; err != nil {
            fmt.Printf("Error saving migrated password for user %s: %v\n", user.Username, err)
        }
    }
    fmt.Println("Password migration completed.")
}

func main() {
	// 1. Initialize Config
	if err := config.Init(); err != nil {
		panic(fmt.Sprintf("Failed to initialize config: %v", err))
	}

	// 2. Initialize Database
	if err := database.Init(); err != nil {
		panic(fmt.Sprintf("Failed to connect to database: %v", err))
	}

    // 3. Run Migrations
    migratePasswords()

	// 4. Setup Router
	r := router.SetupRouter()

	// 5. Start Server
	serverAddr := fmt.Sprintf(":%d", config.Conf.Server.Port)
	fmt.Printf("Go backend server is starting on %s\n", serverAddr)
	if err := r.Run(serverAddr); err != nil {
		panic(err)
	}
}

