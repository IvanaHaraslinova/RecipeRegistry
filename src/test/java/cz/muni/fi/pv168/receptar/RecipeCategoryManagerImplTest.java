/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.receptar;

import DBUtils.SQLScriptManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItem;

/**
 *
 * @author Majo
 */
public class RecipeCategoryManagerImplTest {
    
    private RecipeCategoryManagerImpl catMng;
    private static BasicDataSource dataSource;
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        Properties myconf = new Properties();

        myconf.load(RecipeManagerImplTest.class.getResourceAsStream("/jdbc.properties"));

        dataSource = new BasicDataSource();
        dataSource.setUrl(myconf.getProperty("jdbc.url"));
        dataSource.setUsername(myconf.getProperty("jdbc.user"));
        dataSource.setPassword(myconf.getProperty("jdbc.password"));
    }
    
    
    @Before
    public void setUp() throws SQLException {
        catMng = new RecipeCategoryManagerImpl(dataSource);
        
        SQLScriptManager.executeSqlScript(dataSource, RecipeManagerImplTest.class.getResource("/createTables.sql"));
    }
    
    @After
    public void tearDown() throws SQLException {
        SQLScriptManager.executeSqlScript(dataSource, RecipeManagerImplTest.class.getResource("/dropTables.sql"));
    }

    /**
     * Test of createRecipeCategory method, of class RecipeCategoryManagerImpl.
     */
    @Test
    public void testCreateRecipeCategory() throws Exception {
        RecipeCategory category = new RecipeCategory();
        category.setTitle("Kategoria");
        
        RecipeCategory category2 = catMng.createRecipeCategory(category);
        assertThat(catMng.findAllRecipeCategories(),hasItem(category2));
    }

    /**
     * Test of deleteRecipeCategory method, of class RecipeCategoryManagerImpl.
     */
    @Test
    public void testDeleteRecipeCategory() throws Exception {
        
        RecipeCategory category = new RecipeCategory();
        //Na rozlisenie kvoli obmedzeniam
        category.setTitle("kategoria 1");
        RecipeCategory category2 = new RecipeCategory();
        
        RecipeCategory cat = catMng.createRecipeCategory(category);
        RecipeCategory cat2 = catMng.createRecipeCategory(category2);
        
        Long id1 = cat.getId();
        Long id2 = cat2.getId();
        
        assertNotNull(catMng.findRecipeCategoryById(id1));
        assertNotNull(catMng.findRecipeCategoryById(id2));
        
        catMng.deleteRecipeCategory(cat2);
        
        assertNotNull(catMng.findRecipeCategoryById(id1));
        assertNull(catMng.findRecipeCategoryById(id2));
    }

    /**
     * Test of updateRecipeCategory method, of class RecipeCategoryManagerImpl.
     */
    @Test
    public void testUpdateRecipeCategory() throws Exception {
        RecipeCategory category = new RecipeCategory();
        category.setTitle("Kategoria");
        RecipeCategory cat2 = catMng.createRecipeCategory(category);
        
        assertEquals(cat2, catMng.findRecipeCategoryById(cat2.getId()));
        
        cat2.setTitle("Updatnuta kategoria");
        
        catMng.updateRecipeCategory(cat2);
        
        assertEquals(cat2, catMng.findRecipeCategoryById(cat2.getId()));
    }

    /**
     * Test of findAllRecipeCategories method, of class RecipeCategoryManagerImpl.
     */
    @Test
    public void testFindAllRecipeCategories() throws Exception {
        RecipeCategory category = new RecipeCategory();
        category.setTitle("kategoria 1");
        RecipeCategory cat2 = catMng.createRecipeCategory(category);
        
        RecipeCategory category3 = new RecipeCategory();
        category3.setTitle("kategoria 2");
        RecipeCategory cat4 = catMng.createRecipeCategory(category3);
        
        RecipeCategory category5 = new RecipeCategory();
        category5.setTitle("kategoria 3");
        RecipeCategory cat6 = catMng.createRecipeCategory(category5);
        
        List<RecipeCategory> result = catMng.findAllRecipeCategories();
        
        assertThat(result,hasItem(cat2));
        assertThat(result,hasItem(cat4));
        assertThat(result,hasItem(cat6));
    }

    /**
     * Test of findRecipeCategoryById method, of class RecipeCategoryManagerImpl.
     */
    @Test
    public void testFindRecipeCategoryById() throws Exception {
        RecipeCategory category = new RecipeCategory();
        RecipeCategory cat2 = catMng.createRecipeCategory(category);
        
        assertEquals(cat2, catMng.findRecipeCategoryById(cat2.getId()));
    }
    
}
