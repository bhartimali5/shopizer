package com.salesmanager.shop.model.customer.wishlist;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * Request DTO for adding a product to wishlist.
 * Used in POST requests to create wishlist entries.
 *
 * @author Shopizer Team
 */
public class PersistableWishlist implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Product ID is required")
    private Long productId;

    public PersistableWishlist() {
    }

    public PersistableWishlist(Long productId) {
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
