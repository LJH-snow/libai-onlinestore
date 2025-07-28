package config

import (
	"github.com/spf13/viper"
	"strings"
)

type Config struct {
	Server   ServerConfig   `mapstructure:"server"`
	Database DatabaseConfig `mapstructure:"database"`
	JWT      JWTConfig      `mapstructure:"jwt"`
}

type JWTConfig struct {
	Secret         string `mapstructure:"secret"`
	ExpiresInHours int    `mapstructure:"expires_in_hours"`
}

type ServerConfig struct {
	Port int `mapstructure:"port"`
}

type DatabaseConfig struct {
	Host      string `mapstructure:"host"`
	Port      int    `mapstructure:"port"`
	User      string `mapstructure:"user"`
	Password  string `mapstructure:"password"`
	DBName    string `mapstructure:"dbname"`
	Charset   string `mapstructure:"charset"`
	ParseTime bool   `mapstructure:"parseTime"`
	Loc       string `mapstructure:"loc"`
}

var Conf *Config

func Init() error {
	v := viper.New()

	// Read from config.yaml first
	v.SetConfigFile("config.yaml")
	v.SetConfigType("yaml")
	v.AddConfigPath(".")

	// Enable reading from environment variables
	v.SetEnvKeyReplacer(strings.NewReplacer(".", "_"))
	v.AutomaticEnv()

	// Set default values (optional, but good practice)
	// Server defaults
	v.SetDefault("server.port", 8081)

	// Database defaults
	v.SetDefault("database.host", "localhost")
	v.SetDefault("database.port", 3306)
	v.SetDefault("database.user", "root")
	v.SetDefault("database.password", "123456")
	v.SetDefault("database.dbname", "onlinestore")
	v.SetDefault("database.charset", "utf8mb4")
	v.SetDefault("database.parseTime", true)
	v.SetDefault("database.loc", "Local")

	// JWT defaults
	v.SetDefault("jwt.secret", "a_very_secret_key")
	v.SetDefault("jwt.expires_in_hours", 72)

	// Read config file
	_ = v.ReadInConfig() // Ignore error if config file not found

	// Unmarshal config
	if err := v.Unmarshal(&Conf); err != nil {
		return err
	}

	return nil
}