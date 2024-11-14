package com.example.vehicles.Service;

import com.example.vehicles.Maps.MapsClient;
import com.example.vehicles.car.Car;
import com.example.vehicles.car.CarRepository;
import java.util.List;
import java.util.Optional;

import com.example.vehicles.prices.PriceClient;
import org.springframework.stereotype.Service;
@Service
public class CarService {

    private final CarRepository repository;
    private MapsClient mapsClient;
    private PriceClient priceClient;

    public CarService(CarRepository repository, MapsClient mapClient, PriceClient priceClient) {

        this.repository = repository;
        this.mapsClient = mapClient;
        this.priceClient = priceClient;
    }

    public List<Car> list() {
        return repository.findAll();
    }

    public Car findById(Long id) {

        Car car = this.repository.findById(id).orElseThrow(CarNotFoundException::new);

        car.setPrice(this.priceClient.getPrice(id));

        car.setLocation(this.mapsClient.getAddress(car.getLocation()));

        return car;
    }
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        carToBeUpdated.setCondition(car.getCondition());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    public void delete(Long id) {

        Car carToBeDeleted = this.repository.findById(id).orElseThrow(CarNotFoundException::new);

        this.repository.delete(carToBeDeleted);
    }
}