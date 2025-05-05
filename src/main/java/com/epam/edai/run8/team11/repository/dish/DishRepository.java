package com.epam.edai.run8.team11.repository.dish;

import com.epam.edai.run8.team11.model.dish.Dish;

import java.util.List;
import java.util.Optional;

public interface DishRepository {
    String TABLE_NAME = "dishes";

    Dish save(Dish dish);
    Optional<Dish> findById(String dishId);
    List<Dish> findAll();
    List<Dish> findDishesByLocationId(String locationId);
    List<Dish> findSpecialityDishesByLocationId(String locationId);
    void deleteById(String dishId);
}
