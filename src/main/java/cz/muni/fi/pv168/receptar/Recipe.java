package cz.muni.fi.pv168.receptar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Hello world!
 *
 */
public class Recipe {
    
    private long id;
    private String title = new String();
    private String instructions = new String();
    private String note = new String();
    private RecipeCategory category = new RecipeCategory();
    private List<Ingredience> ingrediences = new ArrayList<>();
    private int time;

    public Recipe() {
    }

    public Recipe(long id, String title, String instructions, String note, RecipeCategory category, List<Ingredience> ingrediences, int time) {
        this.id = id;
        this.title = title;
        this.instructions = instructions;
        this.note = note;
        this.category = category;
        this.ingrediences = ingrediences;
        this.time = time;
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

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public RecipeCategory getCategory() {
        return category;
    }

    public void setCategory(RecipeCategory category) {
        this.category = category;
    }

    public List<Ingredience> getIngrediences() {
        return ingrediences;
    }

    public void setIngrediences(List<Ingredience> ingrediences) {
        this.ingrediences = ingrediences;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 89 * hash + Objects.hashCode(this.title);
        hash = 89 * hash + Objects.hashCode(this.instructions);
        hash = 89 * hash + Objects.hashCode(this.note);
        hash = 89 * hash + Objects.hashCode(this.category);
        hash = 89 * hash + Objects.hashCode(this.ingrediences);
        hash = 89 * hash + this.time;
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
        final Recipe other = (Recipe) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.instructions, other.instructions)) {
            return false;
        }
        if (!Objects.equals(this.note, other.note)) {
            return false;
        }
        if (!Objects.equals(this.category, other.category)) {
            return false;
        }
        if (!Objects.equals(this.ingrediences, other.ingrediences)) {
            return false;
        }
        if (this.time != other.time) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return title;
    }
    
    
    
}
