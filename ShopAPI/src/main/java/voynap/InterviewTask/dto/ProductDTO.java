package voynap.InterviewTask.dto;

import voynap.InterviewTask.models.Keyword;
import voynap.InterviewTask.models.ProductInternal;

import java.util.List;

public class ProductDTO {

    private int id;
    private String name;
    private String description;
    private List<Keyword> keywords;

    private Double price;

    private Integer quantity;

    private List<ProductInternal> internals;

    private int score;

    private String review;

    public ProductDTO() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<ProductInternal> getInternals() {
        return internals;
    }

    public void setInternals(List<ProductInternal> internals) {
        this.internals = internals;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
