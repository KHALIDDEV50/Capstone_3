package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NutritionShoppingListDTO {

    private List<String> proteins;
    private List<String> carbohydrates;
    private List<String> vegetablesAndFruits;
    private List<String> others;
}