package service

import (
	"backend-go/model"
	"backend-go/repository"
	"errors"
)

type AddressInput struct {
	ReceiverName  string `json:"receiverName" binding:"required"`
	Phone         string `json:"phone" binding:"required"`
	Province      string `json:"province" binding:"required"`
	City          string `json:"city" binding:"required"`
	District      string `json:"district" binding:"required"`
	DetailAddress string `json:"detailAddress" binding:"required"`
	IsDefault     bool   `json:"isDefault"`
}

func GetAddressesByUserID(userID uint) ([]model.Address, error) {
	return repository.FindAddressesByUserID(userID)
}

// GetUserAddresses is an alias for GetAddressesByUserID for compatibility
func GetUserAddresses(userID uint) ([]model.Address, error) {
	return GetAddressesByUserID(userID)
}

func CreateAddress(userID uint, input AddressInput) (*model.Address, error) {
	address := &model.Address{
		UserID:        userID,
		ReceiverName:  input.ReceiverName,
		Phone:         input.Phone,
		Province:      input.Province,
		City:          input.City,
		District:      input.District,
		DetailAddress: input.DetailAddress,
		IsDefault:     input.IsDefault,
	}

	if err := repository.CreateAddress(address); err != nil {
		return nil, err
	}

	// If this new address is set as default, update other addresses
	if input.IsDefault {
		if err := repository.SetDefaultAddress(userID, address.ID); err != nil {
			// Log the error but don't fail the whole operation
			// as the address itself was created successfully.
		}
	}

	return address, nil
}

func UpdateAddress(addressID uint, input AddressInput) (*model.Address, error) {
	address, err := repository.FindAddressByID(addressID)
	if err != nil {
		return nil, err
	}

	address.ReceiverName = input.ReceiverName
	address.Phone = input.Phone
	address.Province = input.Province
	address.City = input.City
	address.District = input.District
	address.DetailAddress = input.DetailAddress
	address.IsDefault = input.IsDefault

	if err := repository.UpdateAddress(address); err != nil {
		return nil, err
	}

	if input.IsDefault {
		if err := repository.SetDefaultAddress(address.UserID, address.ID); err != nil {
			// Log error
		}
	}

	return address, nil
}

func DeleteAddress(id uint, userID uint) error {
    // 检查地址是否存在并属于该用户
    address, err := repository.FindAddressByID(id)
    if err != nil {
        return errors.New("address not found")
    }
    if address.UserID != userID {
        return errors.New("you do not have permission to delete this address")
    }

    // 检查地址是否是默认地址
    if address.IsDefault {
        return errors.New("cannot delete the default address")
    }

    // 检查地址是否被任何订单引用
    isReferenced, err := repository.IsAddressReferencedByOrder(id)
    if err != nil {
        return err
    }
    if isReferenced {
        return errors.New("address is referenced by an order and cannot be deleted")
    }

	return repository.DeleteAddress(id)
}

func SetDefaultAddress(userID, addressID uint) error {
	return repository.SetDefaultAddress(userID, addressID)
}
