package com.epam.edai.run8.team11.service.dish;

import com.epam.edai.run8.team11.exception.InvalidInputException;
import com.epam.edai.run8.team11.model.dish.Dish;
import com.epam.edai.run8.team11.repository.dish.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService{

    private final DishRepository dishRepository;

    @Override
    public Optional<Dish> findById(String dishId) {
        if(dishId == null || dishId.isEmpty())
            throw new InvalidInputException(Dish.DISH_ID, dishId);
        return dishRepository.findById(dishId);
    }

    @Override
    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    @Override
    public List<Dish> findSpecialityDishesByLocationId(String locationId) {
        if(locationId == null || locationId.isEmpty())
            throw new InvalidInputException(Dish.LOCATION_ID, locationId);
        return dishRepository.findSpecialityDishesByLocationId(locationId);
    }

}
