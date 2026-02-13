package com.salesmanager.shop.model.customer.wishlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.salesmanager.shop.model.entity.ListCriteria;

/**
 * Response DTO for wishlist collection.
 * Contains list of wishlist items with total count.
 *
 * @author Shopizer Team
 */
public class ReadableWishlistList extends ListCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;
    private List<ReadableWishlist> wishlists = new ArrayList<>();

    public ReadableWishlistList() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<ReadableWishlist> getWishlists() {
        return wishlists;
    }

    public void setWishlists(List<ReadableWishlist> wishlists) {
        this.wishlists = wishlists;
    }
}
