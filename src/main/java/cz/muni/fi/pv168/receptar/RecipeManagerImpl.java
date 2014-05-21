/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.receptar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeManagerImpl implements RecipeManager {

    final static Logger log = LoggerFactory.getLogger(RecipeManagerImpl.class);

    private DataSource dataSource;

    public RecipeManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public RecipeManagerImpl() {
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Recipe createRecipe(Recipe recipe) throws RecipeException {
        log.debug("createRecipe() {}", recipe);
        if (recipe == null) {
            throw new NullPointerException("Recipe in method createRecipe is null");
        }

        if (recipe.getId() != 0) {
            throw new IllegalArgumentException("Recipes id is set - it is probably already used");
        }
//        if(recipe.getTitle() == null){
//            throw new IllegalArgumentException("Recipes title is null");
//        }
//        if(recipe.getInstructions() == null){
//            throw new IllegalArgumentException("Recipes instructions are null");
//        }
//        if(recipe.getNote() == null){
//            throw new IllegalArgumentException("Recipes note is null");
//        }
////        if(recipe.getCategory() == null){
////            throw new IllegalArgumentException("Recipes category is null");
////        }
//        if(recipe.getTime() < 1){
//            throw new IllegalArgumentException("Recipes time is <1");
//        }
//        if(recipe.getIngrediences() == null || recipe.getIngrediences().isEmpty()){
//            throw new IllegalArgumentException("Recipes ingrediences are null or empty");
//        }

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "INSERT INTO recipes (title,instructions,note,category,time) VALUES(?,?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                con.setAutoCommit(false);
                st.setString(1, recipe.getTitle());
                st.setString(2, recipe.getInstructions());
                st.setString(3, recipe.getNote());
                //Len kvoli testom, aby sa to dalo lahsie testovat a nemuseli sme vyplnat aj kategoriu
                if (recipe.getCategory().getId() == 0) {
                    st.setString(4, null);
                } else {
                    st.setLong(4, recipe.getCategory().getId());
                }
                st.setInt(5, recipe.getTime());
                st.executeUpdate();
                try (ResultSet keys = st.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long id = keys.getLong(1);
                        recipe.setId(id);
                    }
                }

                //pridame nove ingredience a nastavime im, ze patria k tomuto receptu
                IngredienceManagerImpl inMng = new IngredienceManagerImpl(dataSource);

                try {
                    for (Ingredience ingred : recipe.getIngrediences()) {
                        inMng.createIngredience(ingred);                    //vytvorenie v DB
                        con.commit();                                       //ciastocny commit, aby fungovala transakcia
                        addIngredienceToRecipe(ingred, recipe);             //pridanie k receptu v DB
                    }
                } catch (IngredienceException e) {
                    log.error("Insert of one of the ingrediences has failed in method createRecipe", e);
                    throw new RecipeException("Insert of recipe has failed - ingredience part");
                }
                con.commit();
            }catch (SQLIntegrityConstraintViolationException e){
                log.error("Insert of recipe has failed in method createRecipe", e);
                throw new RecipeException("Insert of recipe has failed - violation of constraint");
            }


            catch (SQLException e) {
                log.error("Creating of recipe has failed - doing rollback", e);
                con.rollback();
            } finally {
                con.setAutoCommit(true);
            }
            return recipe;
        } catch (SQLException e) {
            log.error("Insert of recipe has failed in method createRecipe", e);
            throw new RecipeException("Insert of recipe has failed");
        }
    }

    @Override
    public void deleteRecipe(Recipe recipe) throws RecipeException {
        log.debug("deleteRecipe()");
        if (recipe == null) {
            throw new NullPointerException("Recipe in method deleteRecipe is null");
        }

        if (recipe.getId() == 0) {
            throw new IllegalArgumentException("Recipes id is not set - it is not in DB");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "DELETE FROM recipes WHERE id=?")) {
                st.setLong(1, recipe.getId());
                st.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Delete of recipe has failed in method deleteRecipe", e);
            throw new RecipeException("Delete of recipe has failed");
        }
    }

    @Override
    public void updateRecipe(Recipe recipe) throws RecipeException {
        log.debug("updateRecipe()");
        if (recipe == null) {
            throw new NullPointerException("Recipe in method updateRecipe is null");
        }
        if (recipe.getId() == 0) {
            throw new IllegalArgumentException("Recipes id is not set - it is not in DB");
        }

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "UPDATE recipes SET title=?,instructions=?,note=?,category=?,time=? WHERE id=?")) {
                st.setString(1, recipe.getTitle());
                st.setString(2, recipe.getInstructions());
                st.setString(3, recipe.getNote());
                //Len kvoli testom, aby sa to dalo lahsie testovat a nemuseli sme vyplnat aj kategoriu
                if (recipe.getCategory().getId() == 0) {
                    st.setString(4, null);
                } else {
                    st.setLong(4, recipe.getCategory().getId());
                }
                st.setInt(5, recipe.getTime());
                st.setLong(6, recipe.getId());
                st.executeUpdate();

                //updatli sme recept samotny, teraz vymazeme ingrediencie v db k nemu a nanovo pridame
                //aktualne z pamate
                try (PreparedStatement st2 = con.prepareStatement(
                        "DELETE FROM ingrediences WHERE recipe=?")) {
                    st2.setLong(1, recipe.getId());
                    st2.executeUpdate();
                }
                //pridame nove ingredience a nastavime im, ze patria k tomuto receptu
                IngredienceManagerImpl inMng = new IngredienceManagerImpl(dataSource);

                for (Ingredience ingred : recipe.getIngrediences()) {
                    inMng.createIngredience(ingred);                    //vytvorenie v DB
                    addIngredienceToRecipe(ingred, recipe);             //pridanie k receptu v DB
                }
            }
        } catch (SQLException e) {
            log.error("Update of recipe has failed in method createRecipe", e);
            throw new RecipeException("Update of recipe has failed");
        } catch (IngredienceException e) {
            log.error("Insert of one of the ingrediences has failed", e);
            throw new RecipeException("Insert of one of the ingrediences has failed");
        }

    }

    @Override
    public List<Recipe> findAllRecipes() throws RecipeException {
        log.debug("findAllRecipes()");
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "SELECT id, title, instructions, note, category, time FROM recipes"
            )) {
                List<Recipe> result = new ArrayList<>();
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        long recipeId = rs.getLong("id");
                        String title = rs.getString("title");
                        String instructions = rs.getString("instructions");
                        String note = rs.getString("note");
                        long categoryId = rs.getLong("category");
                        int time = rs.getInt("time");
                        RecipeCategory category = new RecipeCategory();

                        try (PreparedStatement st2 = con.prepareStatement(
                                "SELECT id, title FROM recipecategories WHERE id=?")) {
                            st2.setLong(1, categoryId);
                            try (ResultSet rs2 = st2.executeQuery()) {
                                if (rs2.next()) {
                                    category.setId(rs2.getLong("id"));
                                    category.setTitle(rs2.getString("title"));
                                }
                            }
                        }
                        List<Ingredience> ingrediences = new ArrayList<>();
                        try (PreparedStatement st3 = con.prepareStatement(
                                "SELECT id, title, amount, unit FROM ingrediences WHERE recipe=?")) {
                            st3.setLong(1, recipeId);
                            try (ResultSet rs3 = st3.executeQuery()) {
                                while (rs3.next()) {
                                    Ingredience ingred = new Ingredience();
                                    ingred.setId(rs3.getLong("id"));
                                    ingred.setTitle(rs3.getString("title"));
                                    ingred.setAmount(rs3.getDouble("amount"));
                                    ingred.setUnit(rs3.getString("unit"));
                                    ingrediences.add(ingred);
                                }
                            }
                        }
                        result.add(new Recipe(recipeId, title, instructions, note, category, ingrediences, time));

                    }
                    return result;

                }

            }

        } catch (SQLException e) {
            log.error("Retrieving all recipes has failed!", e);
            throw new RecipeException("Retrieving all recipes has failed!");
        }
    }

    @Override
    public Recipe findRecipeByTitle(String title) throws RecipeException {
        log.debug("findRecipeByTitle()");
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "SELECT id, title, instructions, note, category, time FROM recipes WHERE title=?"
            )) {
                st.setString(1, title);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        long recipeId = rs.getLong("id");
                        String recipeTitle = rs.getString("title");
                        String instructions = rs.getString("instructions");
                        String note = rs.getString("note");
                        long categoryId = rs.getLong("category");
                        int time = rs.getInt("time");
                        RecipeCategory category = new RecipeCategory();

                        try (PreparedStatement st2 = con.prepareStatement(
                                "SELECT id, title FROM recipecategories WHERE id=?")) {
                            st2.setLong(1, categoryId);
                            try (ResultSet rs2 = st2.executeQuery()) {
                                if (rs2.next()) {
                                    category.setId(rs2.getLong("id"));
                                    category.setTitle(rs2.getString("title"));
                                }
                            }
                        }
                        List<Ingredience> ingrediences = new ArrayList<>();
                        try (PreparedStatement st3 = con.prepareStatement(
                                "SELECT id, title, amount, unit FROM ingrediences WHERE recipe=?")) {
                            st3.setLong(1, recipeId);
                            try (ResultSet rs3 = st3.executeQuery()) {
                                while (rs3.next()) {
                                    Ingredience ingred = new Ingredience();
                                    ingred.setId(rs3.getLong("id"));
                                    ingred.setTitle(rs3.getString("title"));
                                    ingred.setAmount(rs3.getDouble("amount"));
                                    ingred.setUnit(rs3.getString("unit"));
                                    ingrediences.add(ingred);
                                }
                            }
                        }
                        return (new Recipe(recipeId, recipeTitle, instructions, note, category, ingrediences, time));

                    }
                    return null;

                }

            }

        } catch (SQLException e) {
            log.error("Retrieving all recipes has failed!", e);
            throw new RecipeException("Retrieving all recipes has failed!");
        }
    }

    @Override
    public List<Recipe> findRecipesByCategory(RecipeCategory category) throws RecipeException {
        log.debug("findRecipesByCategory()");
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "SELECT id, title, instructions, note, category, time FROM recipes WHERE category=?"
            )) {
                List<Recipe> result = new ArrayList<>();
                st.setLong(1, category.getId());

                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        long recipeId = rs.getLong("id");
                        String title = rs.getString("title");
                        String instructions = rs.getString("instructions");
                        String note = rs.getString("note");
                        long categoryId = rs.getLong("category");
                        int time = rs.getInt("time");
                        RecipeCategory recipeCategory = new RecipeCategory();

                        try (PreparedStatement st2 = con.prepareStatement(
                                "SELECT id, title FROM recipecategories WHERE id=?")) {
                            st2.setLong(1, categoryId);
                            try (ResultSet rs2 = st2.executeQuery()) {
                                if (rs2.next()) {
                                    recipeCategory.setId(rs2.getLong("id"));
                                    recipeCategory.setTitle(rs2.getString("title"));
                                }
                            }
                        }
                        List<Ingredience> ingrediences = new ArrayList<>();
                        try (PreparedStatement st3 = con.prepareStatement(
                                "SELECT id, title, amount, unit FROM ingrediences WHERE recipe=?")) {
                            st3.setLong(1, recipeId);
                            try (ResultSet rs3 = st3.executeQuery()) {
                                while (rs3.next()) {
                                    Ingredience ingred = new Ingredience();
                                    ingred.setId(rs3.getLong("id"));
                                    ingred.setTitle(rs3.getString("title"));
                                    ingred.setAmount(rs3.getDouble("amount"));
                                    ingred.setUnit(rs3.getString("unit"));
                                    ingrediences.add(ingred);
                                }
                            }
                        }
                        result.add(new Recipe(recipeId, title, instructions, note, recipeCategory, ingrediences, time));

                    }
                    return result;

                }

            }

        } catch (SQLException e) {
            log.error("Retrieving all recipes has failed!", e);
            throw new RecipeException("Retrieving all recipes has failed!");
        }
    }

    @Override
    public List<Recipe> findRecipesByIngredienceTitle(Ingredience ingredience) throws RecipeException {
        log.debug("findRecipesByIngredienceTitle()");
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "SELECT recipe FROM ingrediences WHERE title=?"
            )) {
                st.setString(1, ingredience.getTitle());
                List<Recipe> result = new ArrayList<>();

                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        long recipeId = rs.getLong("recipe");
                        Recipe recipe = findRecipeById(recipeId);
                        result.add(recipe);
                    }
                }
                return result;
            }
        } catch (SQLException e) {
            log.error("Retrieving recipes by ingredience title has failed!", e);
            throw new RecipeException("Retrieving recipes by ingredience title has failed!");
        }
    }

    @Override
    public List<Recipe> findRecipesByTime(int time) throws RecipeException {
        log.debug("findRecipesByTime()");
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "SELECT id, title, instructions, note, category, time FROM recipes WHERE time=?"
            )) {
                st.setInt(1, time);
                List<Recipe> result = new ArrayList<>();
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        long recipeId = rs.getLong("id");
                        String recipeTitle = rs.getString("title");
                        String instructions = rs.getString("instructions");
                        String note = rs.getString("note");
                        long categoryId = rs.getLong("category");
                        int time1 = rs.getInt("time");
                        RecipeCategory category = new RecipeCategory();

                        try (PreparedStatement st2 = con.prepareStatement(
                                "SELECT id, title FROM recipecategories WHERE id=?")) {
                            st2.setLong(1, categoryId);
                            try (ResultSet rs2 = st2.executeQuery()) {
                                if (rs2.next()) {
                                    category.setId(rs2.getLong("id"));
                                    category.setTitle(rs2.getString("title"));
                                }
                            }
                        }
                        List<Ingredience> ingrediences = new ArrayList<>();
                        try (PreparedStatement st3 = con.prepareStatement(
                                "SELECT id, title, amount, unit FROM ingrediences WHERE recipe=?")) {
                            st3.setLong(1, recipeId);
                            try (ResultSet rs3 = st3.executeQuery()) {
                                while (rs3.next()) {
                                    Ingredience ingred = new Ingredience();
                                    ingred.setId(rs3.getLong("id"));
                                    ingred.setTitle(rs3.getString("title"));
                                    ingred.setAmount(rs3.getDouble("amount"));
                                    ingred.setUnit(rs3.getString("unit"));
                                    ingrediences.add(ingred);
                                }
                            }
                        }
                        result.add(new Recipe(recipeId, recipeTitle, instructions, note, category, ingrediences, time1));

                    }
                    return result;
                }
            }
        } catch (SQLException e) {
            log.error("Retrieving all recipes by time has failed!", e);
            throw new RecipeException("Retrieving all recipes by time has failed!");
        }
    }

    @Override
    public Recipe findRecipeById(long id) throws RecipeException {
        log.debug("findRecipeById()");
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "SELECT id, title, instructions, note, category, time FROM recipes WHERE id=?"
            )) {
                st.setLong(1, id);

                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        long recipeId = rs.getLong("id");
                        String title = rs.getString("title");
                        String instructions = rs.getString("instructions");
                        String note = rs.getString("note");
                        long categoryId = rs.getLong("category");
                        int time = rs.getInt("time");
                        RecipeCategory recipeCategory = new RecipeCategory();

                        try (PreparedStatement st2 = con.prepareStatement(
                                "SELECT id, title FROM recipecategories WHERE id=?")) {
                            st2.setLong(1, categoryId);
                            try (ResultSet rs2 = st2.executeQuery()) {
                                if (rs2.next()) {
                                    recipeCategory.setId(rs2.getLong("id"));
                                    recipeCategory.setTitle(rs2.getString("title"));
                                }
                            }
                        }
                        List<Ingredience> ingrediences = new ArrayList<>();
                        try (PreparedStatement st3 = con.prepareStatement(
                                "SELECT id, title, amount, unit FROM ingrediences WHERE recipe=?")) {
                            st3.setLong(1, recipeId);
                            try (ResultSet rs3 = st3.executeQuery()) {
                                while (rs3.next()) {
                                    Ingredience ingred = new Ingredience();
                                    ingred.setId(rs3.getLong("id"));
                                    ingred.setTitle(rs3.getString("title"));
                                    ingred.setAmount(rs3.getDouble("amount"));
                                    ingred.setUnit(rs3.getString("unit"));
                                    ingrediences.add(ingred);
                                }
                            }
                        }
                        return (new Recipe(recipeId, title, instructions, note, recipeCategory, ingrediences, time));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Retrieving all recipes has failed!", e);
            throw new RecipeException("Retrieving all recipes has failed!");
        }
        return null;
    }

    @Override
    public void addIngredienceToRecipe(Ingredience ingredience, Recipe recipe) throws RecipeException {
        log.debug("addIngredienceToRecipe()");
        if (ingredience == null) {
            throw new NullPointerException("Ingredience in method addIngredienceToRecipe is null");
        }

        if (recipe == null) {
            throw new NullPointerException("Recipe in method addIngredienceToRecipe is null");
        }

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "UPDATE ingrediences SET recipe=? WHERE id=?")) {
                st.setLong(1, recipe.getId());
                st.setLong(2, ingredience.getId());
                int nrows = st.executeUpdate();
                if (nrows != 1) {
                    throw new RecipeException("While addingIngredienceToRecipe was count of the rows updated !=1");
                }
            }
        } catch (SQLException e) {
            log.error("Adding ingredience to recipe has failed in method addIngredienceToRecipe", e);
            throw new RecipeException("Adding ingredience to recipe has failed in method addIngredienceToRecipe");
        }
    }

    /**
     * Tuto METODU ASI ANI NEBUDE TREBA, staci ak v pamati sa zmeni recept a zavola sa
     * Update() - ten vsetky ingrediencie zmaze a vytvori nanovo
     * Tato metoda to zmaze aj z receptu v pamati aj z receptu v DB
     * @param ingredience
     * @param recipe
     * @throws RecipeException 
     */
    
    @Override
    public void removeIngredienceFromRecipe(Ingredience ingredience, Recipe recipe) throws RecipeException {
        log.debug("removeIngredienceFromRecipe()");
        if (ingredience == null) {
            throw new NullPointerException("Ingredience in method removeIngredienceFromRecipe is null");
        }

        if (recipe == null) {
            throw new NullPointerException("Recipe in method removeIngredienceFromRecipe is null");
        }

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "DELETE FROM ingrediences WHERE id=?")) {
                st.setLong(1, ingredience.getId());
                int nrows = st.executeUpdate();
                if (nrows != 1) {
                    throw new RecipeException("While removingIngredienceToRecipe was count of the rows deleted !=1");
                }
                recipe.getIngrediences().remove(ingredience);
            }
        } catch (SQLException e) {
            log.error("Removing ingredience from recipe has failed in method addIngredienceToRecipe", e);
            throw new RecipeException("Adding ingredience to recipe has failed in method addIngredienceToRecipe");
        }
    }

}
