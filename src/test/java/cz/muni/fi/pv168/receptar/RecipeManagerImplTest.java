/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.receptar;

import DBUtils.SQLScriptManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import static org.hamcrest.CoreMatchers.not;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import static org.junit.matchers.JUnitMatchers.hasItem;

/**
 *
 * @author Majo
 */
public class RecipeManagerImplTest {
    
    private RecipeManagerImpl recipeManager;
    private IngredienceManagerImpl ingredMng;
    private RecipeCategoryManagerImpl catMng;
    private static BasicDataSource dataSource;
    
    @BeforeClass
    public static void setUpClass() throws IOException{
        Properties myconf = new Properties();

        myconf.load(RecipeManagerImplTest.class.getResourceAsStream("/jdbc.properties"));

        dataSource = new BasicDataSource();
        dataSource.setUrl(myconf.getProperty("jdbc.url"));
        dataSource.setUsername(myconf.getProperty("jdbc.user"));
        dataSource.setPassword(myconf.getProperty("jdbc.password"));
    }
    
    @Before
    public void setUp() throws SQLException {
        recipeManager = new RecipeManagerImpl(dataSource);
        ingredMng = new IngredienceManagerImpl(dataSource);
        catMng = new RecipeCategoryManagerImpl(dataSource);
        
        SQLScriptManager.executeSqlScript(dataSource, RecipeManagerImplTest.class.getResource("/createTables.sql"));
//        try(Connection con = dataSource.getConnection()){
//            try(PreparedStatement st = con.prepareStatement(
//                    "CREATE TABLE recipeCategories (" +
//                "id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
//                "title VARCHAR(70) UNIQUE)"
//            )){
//                st.executeUpdate();
//            }
//            try(PreparedStatement st = con.prepareStatement(
//                "CREATE TABLE recipes (" +
//                "id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
//                "title VARCHAR(70)," +
//                "instructions VARCHAR(4000)," +
//                "note VARCHAR(200)," +
//                "category BIGINT REFERENCES recipeCategories(id)," +
//                "time INT)"
//            )){
//                st.executeUpdate();
//            }
//            try(PreparedStatement st = con.prepareStatement(
//                "CREATE TABLE ingrediences (" +
//                "id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
//                "title VARCHAR(70)," +
//                "amount DOUBLE," +
//                "unit VARCHAR(15)," +
//                "recipe BIGINT REFERENCES recipes(id) ON DELETE CASCADE)"
//            )){
//                st.executeUpdate();
//            }
//            
//        } 
    }
    
    @After
    public void tearDown() throws SQLException {
        
        SQLScriptManager.executeSqlScript(dataSource, RecipeManagerImplTest.class.getResource("/dropTables.sql"));
//        try(Connection con = dataSource.getConnection()){
//            try(PreparedStatement st = con.prepareStatement(
//                    "DROP TABLE ingrediences"
//            )){
//                st.executeUpdate();
//            }
//            try(PreparedStatement st = con.prepareStatement(
//                "DROP TABLE recipes"
//            )){
//                st.executeUpdate();
//            }
//            try(PreparedStatement st = con.prepareStatement(
//                "DROP TABLE recipecategories"
//            )){
//                st.executeUpdate();
//            }
//        } 
        
    }

    /**
     * Test of createRecipe method, of class RecipeManagerImpl.
     */
    @Test
    public void testCreateRecipeWithNull() throws Exception{
        try {
            recipeManager.createRecipe(null);
            fail("nevyhodil NullPointerException pre prazdny vstup");
        } catch (NullPointerException ex) {
        }
    }

    /**
     * Test of createRecipe method, of class RecipeManagerImpl.
     */
    @Test
    public void testCreateRecipeCanBeRetrieved() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setTitle("Kolac");
        recipe.setInstructions("Zatial ziadne instrukcie");
        Recipe recipe2 = recipeManager.createRecipe(recipe);
        assertThat(recipeManager.findAllRecipes(), hasItem(recipe2));
    }

    
    /**
     * Test of updateRecipe method, of class RecipeManagerImpl.
     */
    @Test
    public void testUpdateRecipe() throws Exception{
        Recipe recipe = new Recipe();
        recipe.setTitle("Testovaci zakusok");
        Recipe recipe2 = recipeManager.createRecipe(recipe);            //priradi ID receptu
        assertEquals(recipe2, recipeManager.findRecipeById(recipe2.getId()));
        
        recipe2.setTitle("Upraveny testovaci zakusok");
        recipeManager.updateRecipe(recipe2);                            //Pokusi sa updatnut recept v DB
        assertEquals(recipe2, recipeManager.findRecipeById(recipe2.getId()));   //Porovna, ci recept v pamati je zhodny s receptom v DB
    }    
    
    
    /**
     * Test of updateRecipe method, of class RecipeManagerImpl.
     */
    @Test
    public void testFindRecipesByIngredienceTitle() throws Exception{
        Ingredience ingred = new Ingredience();
        ingred.setTitle("Krystalovy cukor");
        //Ingredience ingred2 = ingredMng.createIngredience(ingred);      //priradi ID ingrediencii a ulozi ju do DB
        
        Recipe recipe = new Recipe();
        recipe.setTitle("Testovaci zakusok");
        recipe.getIngrediences().add(ingred);
        Recipe recipe2 = recipeManager.createRecipe(recipe);            //priradi ID receptu a ulozi ho do DB aj s ingredienciami
        
        //recipeManager.addIngredienceToRecipe(ingred2, recipe2);                       //prida ingredienciu k receptu
        
        List<Recipe> list = recipeManager.findRecipesByIngredienceTitle(ingred);
        assertThat(list,hasItem(recipe2));   //Porovna, ci prave vytvoreny recept je v kolekcii receptov obsahujucich krystalovy cukor
    }
    
    
    /**
     * Test of deleteRecipe method, of class RecipeManagerImpl.
     */
    @Test
    public void testDeleteRecipe() throws Exception{
        
        Recipe rec1 = new Recipe();
        Recipe rec2 = new Recipe();
        //aby sme ich odlisili kvoli obmedzeniu, ze title je UNIQUE
        rec2.setTitle("recept2");
        Recipe recipe1 = recipeManager.createRecipe(rec1);
        Recipe recipe2 = recipeManager.createRecipe(rec2);
        
        Long id1 = recipe1.getId();
        Long id2 = recipe2.getId();
       
        
        assertNotNull(recipeManager.findRecipeById(id1));
        assertNotNull(recipeManager.findRecipeById(id2));
        
        recipeManager.deleteRecipe(recipe2);
        
        assertNotNull(recipeManager.findRecipeById(id1));
        assertNull(recipeManager.findRecipeById(id2));
    }
        
       
    /**
     * Test of findRecipesByCategory method, of class RecipeManagerImpl.
     */
    @Test
    public void testfindRecipesByCategory() throws Exception{
        
        RecipeCategory cat = new RecipeCategory();
        cat.setTitle("obed");
        RecipeCategory category = catMng.createRecipeCategory(cat);
        
        Recipe rec = new Recipe();
        rec.setCategory(category);
        Recipe recipe = recipeManager.createRecipe(rec);
        
        
        List <Recipe> recipes = recipeManager.findRecipesByCategory(category);
        
        assertThat(recipes, hasItem(recipe));  
    }
    
    
    /**
     * Test of addIngredienceToRecipe method, of class RecipeManagerImpl.
     */
    @Test
    public void testAddIngredienceToRecipe() throws Exception{
        Ingredience muka = new Ingredience();
        muka.setTitle("muka");
        //Ingredience muka1 = ingredMng.createIngredience(muka);
        
        List<Ingredience> ingrediences = new ArrayList<>();
        ingrediences.add(muka);
        
        Recipe recipe = new Recipe();
        recipe.setIngrediences(ingrediences);
        Recipe recipe1 = recipeManager.createRecipe(recipe);
        //recipeManager.addIngredienceToRecipe(muka1,recipe1);
        
        List<Recipe> recipes = recipeManager.findRecipesByIngredienceTitle(muka);
        assertThat(recipes, hasItem(recipe1));
        
    }
    
    
    /**
     * Test of findAllRecipes method, of class RecipeManagerImpl.
     */
    @Test
    public void testFindAllRecipes() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setTitle("1. recept");
        Recipe rec2 = recipeManager.createRecipe(recipe);
        
        Recipe recipe3 = new Recipe();
        recipe3.setTitle("2. recept");
        Recipe rec4 = recipeManager.createRecipe(recipe3);
        
        Recipe recipe5 = new Recipe();
        recipe5.setTitle("3. recept");
        Recipe rec6 = recipeManager.createRecipe(recipe5);
        
        List<Recipe> result = recipeManager.findAllRecipes();
        
        assertThat(result,hasItem(rec2));
        assertThat(result,hasItem(rec4));
        assertThat(result,hasItem(rec6));
    
    }
    
    
    /**
     * Test of findRecipeByTitle method, of class RecipeManagerImpl.
     */
    @Test
    public void testFindRecipeByTitle() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setTitle("Recept OK");
        Recipe rec2 = recipeManager.createRecipe(recipe);
        
        Recipe recipe3 = new Recipe();
        recipe3.setTitle("Recept NOT OK");
        Recipe rec4 = recipeManager.createRecipe(recipe3);
        
        Recipe result = recipeManager.findRecipeByTitle("Recept OK");
        
        assertEquals(result,rec2);
    }
    
    
    /**
     * Test of findRecipesByTime method, of class RecipeManagerImpl.
     */
    @Test
    public void testFindRecipesByTime() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setTitle("Recept 120");
        recipe.setTime(120);
        Recipe rec2 = recipeManager.createRecipe(recipe);
        
        Recipe recipe3 = new Recipe();
        recipe3.setTitle("Recept 121");
        recipe3.setTime(121);
        Recipe rec4 = recipeManager.createRecipe(recipe3);
        
        List<Recipe> result = recipeManager.findRecipesByTime(120);
        
        assertThat(result,hasItem(rec2));
        assertThat(result,not(hasItem(rec4)));
    }
    
    /**
     * Test of findRecipeById method, of class RecipeManagerImpl.
     */
    @Test
    public void testFindRecipeById() throws Exception {
        Recipe recipe = new Recipe();
        Recipe rec2 = recipeManager.createRecipe(recipe);
        
        assertEquals(rec2, recipeManager.findRecipeById(rec2.getId()));
    }
    
    
    /**
     * Test of removeIngredienceFromRecipe method, of class RecipeManagerImpl.
     */
    @Test
    public void testRemoveIngredienceFromRecipe() throws Exception {
        Recipe recipe = new Recipe();
        
        Ingredience ingredience = new Ingredience();
        ingredience.setTitle("Tato ingrediencia ma zostat na konci testu");
        
        Ingredience ingredience2 = new Ingredience();
        ingredience2.setTitle("Tato ingrediencia ma byt vymazana na konci testu");
        
        List<Ingredience> ingrediences = new ArrayList<>();
        ingrediences.add(ingredience);
        ingrediences.add(ingredience2);
        recipe.setIngrediences(ingrediences);
        
        recipeManager.createRecipe(recipe);
        //Ziskame ho z db
        Recipe rec2 = recipeManager.findRecipeById(recipe.getId());
        //Pozrieme, ci ma obe ingrediencie
        assertThat(rec2.getIngrediences(),hasItem(ingredience));
        assertThat(rec2.getIngrediences(),hasItem(ingredience2));
        
        
        recipeManager.removeIngredienceFromRecipe(ingredience2, recipe);
        
        rec2 = recipeManager.findRecipeById(rec2.getId());
        
        assertThat(rec2.getIngrediences(),hasItem(ingredience));
        assertThat(rec2.getIngrediences(),not(hasItem(ingredience2)));
    
    }
    
}
