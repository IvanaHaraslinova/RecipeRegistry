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

public class IngredienceManagerImpl implements IngredienceManager {

    final static Logger log = LoggerFactory.getLogger(RecipeManagerImpl.class);

    private DataSource dataSource;

    public IngredienceManagerImpl() {
    }

    public IngredienceManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Ingredience createIngredience(Ingredience ingredience) throws IngredienceException {
        log.debug("createIngredience()");
        if (ingredience == null) {
            throw new NullPointerException("Ingredience in method createIngredience is null");
        }

//        if(ingredience.getId() != 0){
//            throw new IllegalArgumentException("Ingredience id is set - it is probably already used");
//        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "INSERT INTO ingrediences (title,amount,unit) VALUES(?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                st.setString(1, ingredience.getTitle());
                st.setDouble(2, ingredience.getAmount());
                st.setString(3, ingredience.getUnit());
                st.executeUpdate();
                try (ResultSet keys = st.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long id = keys.getLong(1);
                        ingredience.setId(id);
                    }
                    return ingredience;
                }
            }
        } catch (SQLException e) {
            log.error("Insert of ingredience has failed in method createIngredience", e);
            throw new IngredienceException("Insert of ingredience has failed");
        }
    }

    @Override
    public void deleteIngredience(Ingredience ingredience) throws IngredienceException {
        log.debug("deleteIngredience()");
        if (ingredience == null) {
            throw new NullPointerException("Ingredience in method deleteIngredience is null");
        }

        if (ingredience.getId() == 0) {
            throw new IllegalArgumentException("Ingrediences id is not set - it is not in DB");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "DELETE FROM ingrediences WHERE id=?")) {
                st.setLong(1, ingredience.getId());
                st.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Delete of ingredience has failed in method deleteIngredience", e);
            throw new IngredienceException("Delete of ingredience has failed");
        }
    }

    @Override
    public void updateIngredience(Ingredience ingredience) throws IngredienceException {
        log.debug("updateIngredience()");
        if (ingredience == null) {
            throw new NullPointerException("Ingredience in method updateIngredience is null");
        }

        if (ingredience.getId() == 0) {
            throw new IllegalArgumentException("Ingrediences id is not set - it is not in DB");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "UPDATE ingrediences SET title=?,amount=?,unit=? WHERE id=?")) {
                st.setString(1, ingredience.getTitle());
                st.setDouble(2, ingredience.getAmount());
                st.setString(3, ingredience.getUnit());
                st.setLong(4, ingredience.getId());
                st.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Update of ingredience has failed in method updateIngredience", e);
            throw new IngredienceException("Update of ingredience has failed");
        }
    }

    @Override
    public Ingredience findIngredienceById(long id) throws IngredienceException {
        log.debug("findIngredienceById()");
        if (id < 1) {
            throw new IllegalArgumentException("Wrong id of ingredience < 1");
        }
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "Select id,title,amount,unit FROM ingrediences WHERE id=?")) {
                st.setLong(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        return new Ingredience(rs.getLong("id"),
                                rs.getString("title"),
                                rs.getDouble("amount"),
                                rs.getString("unit"));
                    }

                }
            }
        } catch (SQLException e) {
            log.error("Retrieve of ingredience has failed in method findIngredienceById", e);
            throw new IngredienceException("Retrieve of ingredience has failed");
        }

        return null;
    }

    @Override
    public List<Ingredience> findIngrediencesByTitle(String title) throws IngredienceException {
        log.debug("findIngrediencesByTitle()");
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "SELECT id, title, amount, unit FROM ingrediences WHERE title=?"
            )) {
                List<Ingredience> result = new ArrayList<>();
                st.setString(1, title);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        long ingredienceId = rs.getLong("id");
                        String ingredienceTitle = rs.getString("title");
                        double amount = rs.getDouble("amount");
                        String unit = rs.getString("unit");
                        result.add(new Ingredience(ingredienceId, ingredienceTitle, amount, unit));
                    }
                    return result;
                }
            }
        } catch (SQLException e) {
            log.error("Retrieving ingredience by title has failed!", e);
            throw new IngredienceException("Retrieving ingredience by title has failed!");
        }
    }

    @Override
    public List<Ingredience> findAllIngrediences() throws IngredienceException {
        log.debug("findAllIngrediences()");
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement(
                    "SELECT id, title, amount, unit FROM ingrediences"
            )) {
                List<Ingredience> result = new ArrayList<>();
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        long ingredienceId = rs.getLong("id");
                        String ingredienceTitle = rs.getString("title");
                        double amount = rs.getDouble("amount");
                        String unit = rs.getString("unit");
                        result.add(new Ingredience(ingredienceId, ingredienceTitle, amount, unit));
                    }
                    return result;
                }
            }
        } catch (SQLException e) {
            log.error("Retrieving all ingrediences has failed!", e);
            throw new IngredienceException("Retrieving all ingrediences has failed!");
        }
    }
}
