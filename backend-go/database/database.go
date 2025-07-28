package database

import (
	"fmt"
	"backend-go/config"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
)

var DB *gorm.DB

func Init() error {
	var err error
	dsn := fmt.Sprintf("%s:%s@tcp(%s:%d)/%s?charset=%s&parseTime=%t&loc=%s",
		config.Conf.Database.User,
		config.Conf.Database.Password,
		config.Conf.Database.Host,
		config.Conf.Database.Port,
		config.Conf.Database.DBName,
		config.Conf.Database.Charset,
		config.Conf.Database.ParseTime,
		config.Conf.Database.Loc,
	)

	DB, err = gorm.Open(mysql.Open(dsn), &gorm.Config{})

	if err != nil {
		return err
	}

	fmt.Println("Database connection successful.")
	return nil
}

