package repository

import (
	"backend-go/database"
	"backend-go/model"
	"gorm.io/gorm"
)

func FindAddressesByUserID(userID uint) ([]model.Address, error) {
	var addresses []model.Address
	if err := database.DB.Where("user_id = ?", userID).Find(&addresses).Error; err != nil {
		return nil, err
	}
	return addresses, nil
}

func FindAddressByID(id uint) (*model.Address, error) {
	var address model.Address
	if err := database.DB.First(&address, id).Error; err != nil {
		return nil, err
	}
	return &address, nil
}

func CreateAddress(address *model.Address) error {
	return database.DB.Create(address).Error
}

func UpdateAddress(address *model.Address) error {
	return database.DB.Save(address).Error
}

func DeleteAddress(id uint) error {
	return database.DB.Delete(&model.Address{}, id).Error
}

func IsAddressReferencedByOrder(addressID uint) (bool, error) {
    var count int64
    err := database.DB.Model(&model.Order{}).Where("address_id = ?", addressID).Count(&count).Error
    if err != nil {
        return false, err
    }
    return count > 0, nil
}

// SetDefaultAddress sets a specific address as default for a user,
// and unsets the previous default.
func SetDefaultAddress(userID, addressID uint) error {
	return database.DB.Transaction(func(tx *gorm.DB) error {
		// Unset the old default address
		if err := tx.Model(&model.Address{}).Where("user_id = ? AND is_default = ?", userID, true).Update("is_default", false).Error; err != nil {
			return err
		}

		// Set the new default address
		if err := tx.Model(&model.Address{}).Where("id = ? AND user_id = ?", addressID, userID).Update("is_default", true).Error; err != nil {
			return err
		}

		return nil
	})
}
