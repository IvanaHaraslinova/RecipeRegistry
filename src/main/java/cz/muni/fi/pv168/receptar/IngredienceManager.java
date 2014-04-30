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
public interface IngredienceManager {
    
    public Ingredience createIngredience(Ingredience ingredience) throws IngredienceException;
    
    public void deleteIngredience(Ingredience ingredience) throws IngredienceException;
    
    public void updateIngredience(Ingredience ingredience) throws IngredienceException;
    
    public Ingredience findIngredienceById(long id) throws IngredienceException;
    
    public List<Ingredience> findIngrediencesByTitle(String title) throws IngredienceException;
    
    public List<Ingredience> findAllIngrediences() throws IngredienceException;
}
