package voynap.InterviewTask.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "discount")
public class Discount {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "size")
    @Min(1)
    @Max(99)
    private Integer size;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "till_date")
    private Date tillDate;

    @OneToMany(mappedBy = "discount")
    private List<Product> products;

    public Discount() {
    }

    public int getId() {
        return id;
    }


    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Date getTillDate() {
        return tillDate;
    }

    public void setTillDate(Date tillDate) {
        this.tillDate = tillDate;
    }

    public void setTillDate(Long hours) {
        this.tillDate = tillDate;
    }


    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }


}
