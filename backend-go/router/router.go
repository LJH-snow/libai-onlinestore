package router

import (
	"backend-go/handler"
	"backend-go/middleware"
	"github.com/gin-gonic/gin"
	"github.com/gin-contrib/cors"
	"time"
)

// SetupRouter configures the routes for the application.
func SetupRouter() *gin.Engine {
	r := gin.Default()

	// CORS middleware
	r.Use(cors.New(cors.Config{
		AllowOrigins:     []string{"http://localhost:8081", "http://localhost:8080", "http://localhost:3000"},
		AllowMethods:     []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowHeaders:     []string{"Origin", "Content-Type", "Accept", "Authorization"},
		ExposeHeaders:    []string{"Content-Length"},
		AllowCredentials: true,
		MaxAge:           12 * time.Hour,
	}))

	// API group
	api := r.Group("/api")
	{
		// --- Public Routes ---
		// Test endpoint
		api.GET("/test/users", handler.TestUsers)

		// Authentication
		api.POST("/register", handler.Register)
		api.POST("/login", handler.Login)

		// Categories & Books
		api.GET("/categories", handler.GetAllCategories)
		api.GET("/books", handler.ListBooks)
		api.GET("/books/:id", handler.GetBook)

		// --- Authenticated Routes (for any logged-in user) ---
		authRequired := api.Group("/")
				authRequired.Use(middleware.AuthMiddleware())
		{
			// User Profile Management
			authRequired.PUT("/user/profile", handler.UpdateUserProfile)
			authRequired.GET("/user/profile", handler.GetUserProfile)

			// Address Management
			authRequired.GET("/addresses", handler.ListAddresses)
			authRequired.POST("/addresses", handler.CreateAddress)
			authRequired.PUT("/addresses/:id", handler.UpdateAddress)
			authRequired.DELETE("/addresses/:id", handler.DeleteAddress)
			authRequired.PATCH("/addresses/:id/default", handler.SetDefaultAddress)

			// Cart Management
			authRequired.GET("/cart", handler.GetCart)
			authRequired.POST("/cart/items", handler.AddToCart)
			authRequired.PUT("/cart/items/:itemId", handler.UpdateCartItemQuantity)
			authRequired.DELETE("/cart/items/:itemId", handler.RemoveFromCart)
			authRequired.DELETE("/cart/clear", handler.ClearCart)

			// Order Management
			authRequired.POST("/orders", handler.CreateOrder)
			authRequired.GET("/orders", handler.ListOrders)
			authRequired.GET("/orders/:id", handler.GetOrderDetails)
			authRequired.PUT("/orders/:id/cancel", handler.CancelOrder) // New route for user to cancel their own order
            authRequired.DELETE("/orders/:id", handler.DeleteOrderForUser)
		}

		// Admin Routes (require ADMIN role)
		adminRequired := api.Group("/admin") // Changed to /admin prefix
		adminRequired.Use(middleware.AuthMiddleware(), middleware.AdminMiddleware())
		{
			// Category Management
			adminRequired.POST("/categories", handler.CreateCategory)
			adminRequired.PUT("/categories/:id", handler.UpdateCategory)
			adminRequired.DELETE("/categories/:id", handler.DeleteCategory)

			// Book Management
			adminRequired.GET("/books", handler.ListBooksForAdmin)
			adminRequired.POST("/books", handler.CreateBook)
			adminRequired.PUT("/books/:id", handler.UpdateBook)
			adminRequired.DELETE("/books/:id", handler.DeleteBook)
			adminRequired.PUT("/books/:id/status", handler.UpdateBookStatus)

			// Order Management (Admin)
			adminRequired.GET("/orders", handler.ListAllOrders)
			adminRequired.PUT("/orders/:id/status", handler.UpdateOrderStatus)
			adminRequired.DELETE("/orders/:id", handler.DeleteOrder)

			// User Management
			adminRequired.GET("/users", handler.ListUsers) // Updated to use the enhanced ListUsers
			adminRequired.DELETE("/users/:id", handler.DeleteUser)
			adminRequired.PUT("/users/:id/status", handler.UpdateUserStatus) // New: Update user status
			adminRequired.POST("/users", handler.CreateUserByAdmin) // New: Create user by admin
		}

		// Add these routes to match frontend expectations
		api.GET("/order/user/:userId", middleware.AuthMiddleware(), handler.GetUserOrders)
		api.GET("/order/:id", middleware.AuthMiddleware(), handler.GetOrderDetails)
		api.GET("/addresses/user/:userId", middleware.AuthMiddleware(), handler.GetUserAddresses)
	}

	return r
}


