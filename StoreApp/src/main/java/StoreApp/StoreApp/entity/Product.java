package StoreApp.StoreApp.entity;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "product_name", columnDefinition = "nvarchar(1111)")
    private String product_name;

    @Column(name = "description", columnDefinition = "nvarchar(11111)")
    private String description;

    @Column(name = "sold")
    private int sold;

    @Column(name = "is_active")
    private int is_active;

    @Column(name = "is_selling")
    private int is_selling;

    @Column(name = "created_at")
    private Date created_at;

    @Column(name = "price")
    private int price;

    @Column(name = "quantity")
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference ("cart-category")
    private Category category;

    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonBackReference("productImages-product")
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonBackReference("orde_item-product")
    private List<Order_Item> orderItem;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonBackReference ("cart-product")
    private List<Cart> cart;


    @OneToMany(mappedBy = "product")
    @JsonBackReference("product-rating")
    private List<Rating> ratings;
    public int getCategoryId() {
        return category != null ? category.getId() : 1;
    }

    public void setCategoryId(int categoryId) {
        if (this.category == null) {
            this.category = new Category();
        }
        this.category.setId(categoryId);
    }
}