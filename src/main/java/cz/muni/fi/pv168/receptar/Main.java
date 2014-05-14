/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.receptar;

import cz.muni.fi.pv168.gui.Receptar;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

/**
 *
 * @author Majo
 */
public class Main {
    public static void main(String[] args) throws IOException,RecipeException, IngredienceException, RecipeCategoryException {
        Properties myconf = new Properties();
        
        myconf.load(Main.class.getResourceAsStream("/jdbc.properties"));
        
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(myconf.getProperty("jdbc.url"));
        ds.setUsername(myconf.getProperty("jdbc.user"));
        ds.setPassword(myconf.getProperty("jdbc.password"));
        
        myconf.store(System.out, "Property subor vypis:");
        
        System.out.println("");
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                
                frame.add(new Receptar(ds));
                frame.setTitle("Receptar");
                frame.setSize(1000, 800);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                frame.setVisible(true);
            }
        });
        
        
        
//        RecipeManagerImpl recipeManager = new RecipeManagerImpl(ds);
//        
//        Recipe recipe = new Recipe();
//        recipe.setTitle("SKUSOBNY RECEPT Z KODUU");
//        recipe.setInstructions("NEJAKE INSTRUKCIE");
//        recipe.setNote("NEJAKY NOTE");
//        recipe.setTime(1200);
        
        //recipeManager.createRecipe(recipe);
//        
//        recipe.setId(0);
//        recipeManager.createRecipe(recipe);
//        recipe.setId(8);
//        recipeManager.deleteRecipe(recipe);
        
//        for (Recipe recipe2 : recipeManager.findAllRecipes()) {
//            System.out.println(recipe2);
//        }
//        
//        System.out.println(recipeManager.findRecipeByTitle("SKUSOBNY RECEPT Z KODU"));
 
        ///////////////////////////////////////////////////////////////
        
//        IngredienceManagerImpl mng = new IngredienceManagerImpl(ds);
        
//        Ingredience ing = new Ingredience(0, "skusobna ingred", 455.0,"kg");
//        mng.createIngredience(ing);
//        ing = mng.findIngredienceById(3);
//        
        /////////////////////////////////////////////////////////////////////
        
//        IngredienceManagerImpl inMng = new IngredienceManagerImpl(ds);
//        RecipeManagerImpl mng = new RecipeManagerImpl(ds);
//        RecipeCategoryManagerImpl rMng = new RecipeCategoryManagerImpl(ds);
//        RecipeCategory rc = new RecipeCategory(0, "Skusobna kategoria");
//        mng.createRecipeCategory(rc);
        
        
        
        
//        Recipe recept = new Recipe();
//        recept.setTitle("skusobny nazov");
//        recept.setInstructions("skusobne instrukcie");
//        recept.setNote("skusobna poznamka");
//        recept.setTime(120);
//        
//        mng.createRecipe(recept);
//        
//        Ingredience ingred = new Ingredience(0, "skusobna muka", 12.4, "dkg");
//        inMng.createIngredience(ingred);
//        recept.getIngrediences().add(ingred);
//        mng.addIngredienceToRecipe(ingred, recept);
//        
//        Ingredience ingred2 = new Ingredience(0, "skusobny cukor", 11.4, "dkg");
//        inMng.createIngredience(ingred2);
//        recept.getIngrediences().add(ingred2);
//        mng.addIngredienceToRecipe(ingred2, recept);
//        
//        //UPDATE UDAJOV RECEPTU
//        
//        recept.setTitle("updatnuty skusobny nazov");
//        recept.setInstructions("updatnute skusobne instrukcie");
//        recept.setNote("updatnuta skusobna poznamka");
//        recept.setTime(50);
//        
//        Ingredience ingred3 = new Ingredience(0, "skusobny olej", 11.4, "liter");
//        recept.getIngrediences().add(ingred3);
//        
//        mng.updateRecipe(recept);
        
        //System.out.println(mng.findRecipeCategoryById(2));
        
//        IngredienceManagerImpl inMng = new IngredienceManagerImpl(ds);
//        RecipeManagerImpl mng = new RecipeManagerImpl(ds);
//      
//        Recipe recept = new Recipe();
//        recept.setTitle("skusobny nazov pre kategoriu");
//        recept.setInstructions("skusobne instrukcie");
//        recept.setNote("skusobna poznamka");
//        recept.setTime(120);
        //recept.setCategory(new RecipeCategory(1,"skusobna kategoria"));
        
                
//        Ingredience ingred = new Ingredience(0, "skusobna muka", 12.4, "dkg");
        //inMng.createIngredience(ingred);
//        Ingredience ingred2 = new Ingredience(2, "skusobna muka", 12.4, "dkg");
//        recept.getIngrediences().add(ingred);
//        recept.getIngrediences().add(ingred2);
        //mng.addIngredienceToRecipe(ingred, recept);
        
//        mng.createRecipe(recept);
//        System.out.println(recept);
//        System.out.println(mng.findAllRecipes());
        
        
//        mng.removeIngredienceFromRecipe(ingred, recept);
//        
//        System.out.println(recept);
//        System.out.println(mng.findAllRecipes());
        
    }
}
