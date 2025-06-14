package com.epam.edai.run8.team11.service.dish;

import com.epam.edai.run8.team11.exception.InvalidInputException;
import com.epam.edai.run8.team11.exception.dish.DishNotFoundException;
import com.epam.edai.run8.team11.model.dish.Dish;
import com.epam.edai.run8.team11.repository.dish.DishRepository;
import com.epam.edai.run8.team11.service.presignedimage.PresignedImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService{

    private final DishRepository dishRepository;
    private final PresignedImageService imageService;

    @Override
    public Dish findById(String dishId) {
        if(dishId == null || dishId.isEmpty())
            throw new InvalidInputException(Dish.DISH_ID, dishId);
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));
        dish.setImageUrl(imageService.generatePresignedUrlForImage(dish.getImageUrl()));
        return dish;
    }

    @Override
    public List<Dish> findAll() {
        return dishRepository.findAll()
                .stream()
                .peek(dish -> dish.setImageUrl(
                        imageService.generatePresignedUrlForImage(dish.getImageUrl()))
                ).toList();
    }

    @Override
    public List<Dish> findPopularDishes() {
        return findAll().stream()
                .filter(Dish::getIsPopular)
                .toList();
    }

    @Override
    public List<Dish> findSpecialityDishesByLocationId(String locationId) {
        if(locationId == null || locationId.isEmpty())
            throw new InvalidInputException(Dish.LOCATION_ID, locationId);
        return dishRepository.findSpecialityDishesByLocationId(locationId)
                .stream()
                .peek(dish -> dish.setImageUrl(
                        imageService.generatePresignedUrlForImage(dish.getImageUrl()))
                ).toList();
    }

}
