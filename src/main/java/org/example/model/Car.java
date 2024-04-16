package org.example.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CAR")
public class Car extends Vehicle {
    @Id
    private Long id;

    public Car(String brand, String model, int year, double price, String plate) {
        super(brand, model, year, price, plate);
    }
    public Car() {
        //IT IS MANDATORY!!
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
