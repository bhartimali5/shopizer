package com.salesmanager.shop.model.customer.wishlist;

import java.io.Serializable;

import com.salesmanager.shop.model.catalog.product.ReadableProduct;

/**
 * Response DTO for wishlist items.
 * Contains full product details for display.
 *
 * @author Shopizer Team
 */
public class ReadableWishlist implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long customerId;
    private ReadableProduct product;
    private String dateAdded;

    public ReadableWishlist() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public ReadableProduct getProduct() {
        return product;
    }

    public void setProduct(ReadableProduct product) {
        this.product = product;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}
