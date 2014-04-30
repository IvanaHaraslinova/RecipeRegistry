/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.receptar;

import java.util.Objects;

/**
 *
 * @author Majo
 */
public class RecipeCategory {
    
    private long id;
    private String title = new String();

    public RecipeCategory() {
    }

    public RecipeCategory(long id, String title) {
        this.id = id;
        this.title = title;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 67 * hash + Objects.hashCode(this.title);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RecipeCategory other = (RecipeCategory) obj;
        if (this.id != other.id) {
            return false;
        }
        return Objects.equals(this.title, other.title);
    }

    @Override
    public String toString() {
        return "RecipeCategory{" + "id=" + id + ", title=" + title + '}';
    }
    
    
}
