package com.example.vehicles.Controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import com.example.vehicles.Service.CarService;
import com.example.vehicles.car.Car;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
@ApiResponses(value = {
        @ApiResponse(code=400, message="This is a bad request. Please follow the API documentation for proper request."),
        @ApiResponse(code=401, message="Due to security constraints, your access request cannot be authorized."),
        @ApiResponse(code=500, message="The server is down. Please make sure that the Price and Maps microservices are running.")
})
class CarController {

    private final CarService carService;
    private final CarResourceAssembler assembler;

    CarController(CarService carService, CarResourceAssembler assembler) {
        this.carService = carService;
        this.assembler = assembler;
    }

    @GetMapping
    Resources<Resource<Car>> list() {
        List<Resource<Car>> resources = carService.list().stream().map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(resources,
                linkTo(methodOn(CarController.class).list()).withSelfRel());
    }

    @GetMapping("/{id}")
    Resource<Car> get(@PathVariable Long id) {

        Car car = this.carService.findById(id);
        return assembler.toResource(car);
    }

    @PostMapping
    ResponseEntity<?> post(@Valid @RequestBody Car car) throws URISyntaxException {

        car = this.carService.save(car);

        Resource<Car> resource = assembler.toResource(car);
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }


    @PutMapping("/{id}")
    ResponseEntity<?> put(@PathVariable Long id, @Valid @RequestBody Car car) {

        car.setId(id);

        this.carService.save(car);

        Resource<Car> resource = assembler.toResource(car);
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {

        this.carService.delete(id);
        return ResponseEntity.noContent().build();
    }
}