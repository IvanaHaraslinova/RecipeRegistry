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
public interface RecipeCategoryManager {
    
    public RecipeCategory createRecipeCategory(RecipeCategory category) throws RecipeCategoryException;
    
    public void deleteRecipeCategory(RecipeCategory category) throws RecipeCategoryException;
    
    public void updateRecipeCategory(RecipeCategory category) throws RecipeCategoryException;
    
    public List<RecipeCategory> findAllRecipeCategories() throws RecipeCategoryException;
    
    public RecipeCategory findRecipeCategoryById(long id) throws RecipeCategoryException;
}
