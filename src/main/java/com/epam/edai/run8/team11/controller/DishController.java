package com.epam.edai.run8.team11.controller;

import com.epam.edai.run8.team11.model.dish.Dish;
import com.epam.edai.run8.team11.service.dish.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping
    public ResponseEntity<Map<String,Object>> getAllDishes(){
        List<Dish> dishes = dishService.findAll();
        return ResponseEntity.ok(Map.of("content", dishes));
    }

    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularDishes(){
        List<Dish> popularDishes = dishService.findPopularDishes();
        return ResponseEntity.ok(Map.of("dishes", popularDishes));
    }

    @GetMapping("/{id}")
    public Dish getDishById(@PathVariable String id){
        return dishService.findById(id);
    }
}
