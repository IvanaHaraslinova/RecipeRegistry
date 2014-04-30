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
 public class Ingredience {
    
     private long id;
     private String title = new String();
     private double amount;
     private String unit = new String();

    public Ingredience() {
    }

    public Ingredience(long id, String title, double amount, String unit) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.unit = unit;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 61 * hash + Objects.hashCode(this.title);
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.amount) ^ (Double.doubleToLongBits(this.amount) >>> 32));
        hash = 61 * hash + Objects.hashCode(this.unit);
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
        final Ingredience other = (Ingredience) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (Double.doubleToLongBits(this.amount) != Double.doubleToLongBits(other.amount)) {
            return false;
        }
        return Objects.equals(this.unit, other.unit);
    }

    @Override
    public String toString() {
        return "Ingredience{" + "id=" + id + ", title=" + title + ", amount=" + amount + ", unit=" + unit + '}';
    }
     
     
}
