/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.receptar;

import java.util.List;

/**
 *
 * @author Majo
 */
public interface RecipeManager {
    
    public Recipe createRecipe(Recipe recipe) throws RecipeException;
    
    public void deleteRecipe(Recipe recipe) throws RecipeException;
    
    public void updateRecipe(Recipe recipe) throws RecipeException;
    
    public List<Recipe> findAllRecipes() throws RecipeException;
    
    public Recipe findRecipeByTitle(String title) throws RecipeException;
    
    public List<Recipe> findRecipesByCategory(RecipeCategory category) throws RecipeException;
    
    public List<Recipe> findRecipesByIngredienceTitle(Ingredience ingredience) throws RecipeException;
    
    public List<Recipe> findRecipesByTime(int time) throws RecipeException;
    
    public Recipe findRecipeById(long id) throws RecipeException;
    
    public void addIngredienceToRecipe(Ingredience ingredience, Recipe recipe) throws RecipeException;
    
    public void removeIngredienceFromRecipe(Ingredience ingredience, Recipe recipe) throws RecipeException;
    
}
