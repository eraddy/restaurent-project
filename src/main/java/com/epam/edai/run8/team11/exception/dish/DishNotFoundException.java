package com.epam.edai.run8.team11.exception.dish;

public class DishNotFoundException extends RuntimeException{
    public DishNotFoundException(String dishId){
        super("Dish not found with id: " + dishId);
    }
}
