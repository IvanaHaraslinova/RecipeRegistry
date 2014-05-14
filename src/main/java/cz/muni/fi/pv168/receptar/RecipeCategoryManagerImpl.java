/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.receptar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeCategoryManagerImpl implements RecipeCategoryManager {

    final static Logger log = LoggerFactory.getLogger(RecipeManagerImpl.class);

    private DataSource dataSource;

    public RecipeCategoryManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public RecipeCategory createRecipeCategory(RecipeCategory category) throws RecipeCategoryException {
        log.debug("createRecipeCategory()");
        if (category == null) {
            throw new NullPointerException("RecipeCategory in method createRecipeCategory is null");
        }

        if (category.getId() != 0) {
            throw new IllegalArgumentException("RecipeCategory id is set - it is probably already used");
        }

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "INSERT INTO recipecategories (title) VALUES(?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                con.setAutoCommit(false);
                st.setString(1, category.getTitle());
                st.executeUpdate();
                try (ResultSet keys = st.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long id = keys.getLong(1);
                        category.setId(id);
                    }
                }
                con.commit();
            } catch (SQLException e) {
                log.error("Creating of recipeCategory has failed - doing rollback", e);
                con.rollback();
            } finally {
                con.setAutoCommit(true);
            }
            return category;
        } catch (SQLException e) {
            log.error("Insert of RecipeCategory has failed in method createRecipe", e);
            throw new RecipeCategoryException("Insert of RecipeCategory has failed");
        }
    }

    @Override
    public void deleteRecipeCategory(RecipeCategory category) throws RecipeCategoryException {
        log.debug("deleteRecipeCategory()");
        if (category == null) {
            throw new NullPointerException("Ingredience in method deleteIngredience is null");
        }

        if (category.getId() == 0) {
            throw new IllegalArgumentException("Ingrediences id is not set - it is not in DB");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "DELETE FROM recipecategories WHERE id=?")) {
                con.setAutoCommit(false);
                st.setLong(1, category.getId());
                st.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                log.error("Deleting of recipeCategory has failed - doing rollback", e);
                con.rollback();
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("Delete of RecipeCategory has failed in method deleteIngredience", e);
            throw new RecipeCategoryException("Delete of RecipeCategory has failed");
        }
    }

    @Override
    public void updateRecipeCategory(RecipeCategory category) throws RecipeCategoryException {
        log.debug("updateRecipeCategory()");
        if (category == null) {
            throw new NullPointerException("RecipeCategory in method updateRecipeCategory is null");
        }

        if (category.getId() == 0) {
            throw new IllegalArgumentException("RecipeCategory id is not set - it is not in DB");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "UPDATE recipecategories SET title=? WHERE id=?")) {
                con.setAutoCommit(false);
                st.setString(1, category.getTitle());
                st.setLong(2, category.getId());
                st.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                log.error("Updating of recipeCategory has failed - doing rollback", e);
                con.rollback();
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("Update of RecipeCategory has failed in method updateRecipeCategory", e);
            throw new RecipeCategoryException("Update of RecipeCategory has failed");
        }
    }

    @Override
    public List<RecipeCategory> findAllRecipeCategories() throws RecipeCategoryException {
        log.debug("findAllRecipeCategories()");
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "SELECT id, title FROM recipecategories"
            )) {
                List<RecipeCategory> result = new ArrayList<>();
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        long recipeCategoryId = rs.getLong("id");
                        String recipeCategoryTitle = rs.getString("title");
                        result.add(new RecipeCategory(recipeCategoryId, recipeCategoryTitle));
                    }
                    return result;
                }
            }
        } catch (SQLException e) {
            log.error("Retrieving all recipeCategories has failed!", e);
            throw new RecipeCategoryException("Retrieving all recipeCategories has failed!");
        }
    }

    @Override
    public RecipeCategory findRecipeCategoryById(long id) throws RecipeCategoryException {
        log.debug("findRecipeCategoryById()");
        if (id < 1) {
            throw new IllegalArgumentException("Wrong id of RecipeCategory < 1");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "Select id,title FROM recipecategories WHERE id=?")) {
                st.setLong(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        return new RecipeCategory(rs.getLong("id"), rs.getString("title"));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Retrieve of ingredience has failed in method findIngredienceById", e);
            throw new RecipeCategoryException("Retrieve of ingredience has failed");
        }
        return null;
    }
    
    @Override
    public RecipeCategory findRecipeCategoryByTitle(String title) throws RecipeCategoryException{
        log.debug("findRecipeCategoryByTitle()");
        
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "Select id,title FROM recipecategories WHERE title=?")) {
                st.setString(1, title);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        return new RecipeCategory(rs.getLong("id"), rs.getString("title"));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Retrieve of ingredience has failed in method findIngredienceById", e);
            throw new RecipeCategoryException("Retrieve of ingredience has failed");
        }
        return null;
    }
    
    
  

}
