package utils

import (
    "errors"
    "time"

    "github.com/golang-jwt/jwt/v4"
    "github.com/spf13/viper"
)

// Claims represents the JWT claims
type Claims struct {
    UserID uint   `json:"user_id"`
    Email  string `json:"email"`
    Role   string `json:"role"`
    jwt.RegisteredClaims
}

// GenerateJWT generates a new JWT token
func GenerateJWT(userID uint, email, role string) (string, error) {
    claims := Claims{
        UserID: userID,
        Email:  email,
        Role:   role,
        RegisteredClaims: jwt.RegisteredClaims{
            ExpiresAt: jwt.NewNumericDate(time.Now().Add(24 * time.Hour)),
            IssuedAt:  jwt.NewNumericDate(time.Now()),
        },
    }

    token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
    secretKey := viper.GetString("jwt.secret")
    if secretKey == "" {
        secretKey = "your-secret-key" // fallback
    }

    return token.SignedString([]byte(secretKey))
}

// ValidateJWT validates a JWT token and returns the claims
func ValidateJWT(tokenString string) (*Claims, error) {
    secretKey := viper.GetString("jwt.secret")
    if secretKey == "" {
        secretKey = "your-secret-key" // fallback
    }

    token, err := jwt.ParseWithClaims(tokenString, &Claims{}, func(token *jwt.Token) (interface{}, error) {
        return []byte(secretKey), nil
    })

    if err != nil {
        return nil, err
    }

    if claims, ok := token.Claims.(*Claims); ok && token.Valid {
        return claims, nil
    }

    return nil, errors.New("invalid token")
}