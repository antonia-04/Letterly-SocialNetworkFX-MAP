package org.example.socialnetworkfx.domain.validation;

import org.example.socialnetworkfx.domain.User;

public class UserValidation implements Validation<User>{
    //sa se adauge si validare pt nume
    @Override
    public void validate(User entity){
        if(entity.getFirstName().isEmpty() || entity.getLastName().isEmpty()){
            throw new ValidationException("First Name and Last Name can not be empty");
        }
    }

}
