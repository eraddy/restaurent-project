package com.epam.edai.run8.team11.service.dish;

import com.epam.edai.run8.team11.model.dish.Dish;

import java.util.List;
import java.util.Optional;

public interface DishService {
    Dish findById(String dishId);
    List<Dish> findAll();
    List<Dish> findPopularDishes();
    List<Dish> findSpecialityDishesByLocationId(String locationId);
}