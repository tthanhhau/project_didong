package StoreApp.StoreApp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rating")
public class Rating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "rate", columnDefinition = "nvarchar(1111)")
    private int rate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Transient
    @JsonProperty("product_id")
    private Integer productId;

    @Transient
    @JsonProperty("user_id")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference("product-rating")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference("user-rating")
    private User user;

    public Integer getProductId() {
        return product != null ? product.getId() : productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return user != null ? user.getId() : userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return user != null ? user.getUser_Name() : null;
    }
}