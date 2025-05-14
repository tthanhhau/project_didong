package StoreApp.StoreApp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
@JsonIdentityInfo(
	    generator = ObjectIdGenerators.PropertyGenerator.class,
	    property = "id"
	)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "product_Name", columnDefinition = "nvarchar(1111)")
    private String productName;

    @Column(name = "description", columnDefinition = "nvarchar(11111)")
    private String description;

    @Column(name = "sold")
    private int sold;

    @Column(name = "is_Active")
    private int isActive;

    @Column(name = "is_Selling")
    private int isSelling;

    @Column(name = "created_At")
    private Date createdAt;

    @Column(name = "price")
    private int price;

    @Column(name = "quantity")
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @EqualsAndHashCode.Exclude
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Order_Item> orderItem;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Cart> cart;


    @OneToMany(mappedBy = "product")
    @JsonBackReference("product-rating")
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