package com.epam.edai.run8.team11.controller;

import com.epam.edai.run8.team11.model.dish.Dish;
import com.epam.edai.run8.team11.service.dish.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping
    public List<Dish> getAllDishes(){
        return dishService.findAll();
    }

    @GetMapping("/popular")
    public List<Dish> getPopularDishes(){
        return dishService.findPopularDishes();
    }

    @GetMapping("/{id}")
    public Dish getDishById(@PathVariable String id){
        return dishService.findById(id);
    }
}
