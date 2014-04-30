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
import static org.hamcrest.CoreMatchers.not;
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
public class IngredienceManagerImplTest {
    
    private IngredienceManagerImpl ingredMng;
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
        ingredMng = new IngredienceManagerImpl(dataSource);
        
        SQLScriptManager.executeSqlScript(dataSource, RecipeManagerImplTest.class.getResource("/createTables.sql"));
    }
    
    @After
    public void tearDown() throws SQLException {
        SQLScriptManager.executeSqlScript(dataSource, RecipeManagerImplTest.class.getResource("/dropTables.sql"));
    }

    /**
     * Test of createIngredience method, of class IngredienceManagerImpl.
     */
    @Test
    public void testCreateIngredience() throws Exception {
        Ingredience ingredience = new Ingredience();
        ingredience.setTitle("Muka");
        ingredience.setAmount(200);
        ingredience.setUnit("g");
        Ingredience ingred2 = ingredMng.createIngredience(ingredience);
        assertThat(ingredMng.findAllIngrediences(),hasItem(ingred2));
    }

    /**
     * Test of deleteIngredience method, of class IngredienceManagerImpl.
     */
    @Test
    public void testDeleteIngredience() throws Exception {
        
        Ingredience ingredience = new Ingredience();
        Ingredience ingredience2 = new Ingredience();
        
        Ingredience ingred = ingredMng.createIngredience(ingredience);
        Ingredience ingred2 = ingredMng.createIngredience(ingredience2);
        
        Long id1 = ingred.getId();
        Long id2 = ingred2.getId();
        
        assertNotNull(ingredMng.findIngredienceById(id1));
        assertNotNull(ingredMng.findIngredienceById(id2));
        
        ingredMng.deleteIngredience(ingred2);
        
        assertNotNull(ingredMng.findIngredienceById(id1));
        assertNull(ingredMng.findIngredienceById(id2));
    }

    /**
     * Test of updateIngredience method, of class IngredienceManagerImpl.
     */
    @Test
    public void testUpdateIngredience() throws Exception {
        Ingredience ingredience = new Ingredience();
        ingredience.setTitle("muka");
        ingredience.setAmount(200);
        ingredience.setUnit("g");
        Ingredience ingred2 = ingredMng.createIngredience(ingredience);
        
        assertEquals(ingred2, ingredMng.findIngredienceById(ingred2.getId()));
        
        ingred2.setAmount(400);
        
        ingredMng.updateIngredience(ingred2);
        
        assertEquals(ingred2, ingredMng.findIngredienceById(ingred2.getId()));
    }

    /**
     * Test of findIngredienceById method, of class IngredienceManagerImpl.
     */
    @Test
    public void testFindIngredienceById() throws Exception {
        
        Ingredience ingredience = new Ingredience();
        Ingredience ingred2 = ingredMng.createIngredience(ingredience);
        
        assertEquals(ingred2, ingredMng.findIngredienceById(ingred2.getId()));
    }

    /**
     * Test of findIngrediencesByTitle method, of class IngredienceManagerImpl.
     */
    @Test
    public void testFindIngrediencesByTitle() throws Exception {
        
        Ingredience ingredience = new Ingredience();
        ingredience.setTitle("ingrediencia OK");
        Ingredience ingred2 = ingredMng.createIngredience(ingredience);
        
        Ingredience ingredience3 = new Ingredience();
        ingredience3.setTitle("ingrediencia NOT OK");
        Ingredience ingred4 = ingredMng.createIngredience(ingredience3);
        
        List<Ingredience> result = ingredMng.findIngrediencesByTitle("ingrediencia OK");
        
        assertThat(result,hasItem(ingred2));
        assertThat(result,not(hasItem(ingred4)));
    }

    /**
     * Test of findAllIngrediences method, of class IngredienceManagerImpl.
     */
    @Test
    public void testFindAllIngrediences() throws Exception {
        
        Ingredience ingredience = new Ingredience();
        ingredience.setTitle("ingrediencia 1");
        Ingredience ingred2 = ingredMng.createIngredience(ingredience);
        
        Ingredience ingredience3 = new Ingredience();
        ingredience3.setTitle("ingrediencia 2");
        Ingredience ingred4 = ingredMng.createIngredience(ingredience3);
        
        Ingredience ingredience5 = new Ingredience();
        ingredience5.setTitle("ingrediencia 3");
        Ingredience ingred6 = ingredMng.createIngredience(ingredience5);
        
        List<Ingredience> result = ingredMng.findAllIngrediences();
        
        assertThat(result,hasItem(ingred2));
        assertThat(result,hasItem(ingred4));
        assertThat(result,hasItem(ingred6));
    }
    
}
