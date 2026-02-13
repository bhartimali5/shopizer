package com.salesmanager.shop.store.controller.customer.facade;

import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.wishlist.PersistableWishlist;
import com.salesmanager.shop.model.customer.wishlist.ReadableWishlist;
import com.salesmanager.shop.model.customer.wishlist.ReadableWishlistList;

/**
 * Facade interface for wishlist business operations.
 * Provides orchestration between services and handles DTO conversion.
 *
 * @author Shopizer Team
 */
public interface WishlistFacade {

    /**
     * Add a product to customer's wishlist.
     * If product already exists in wishlist, returns the existing entry.
     *
     * @param customer The customer
     * @param wishlist The wishlist request containing product ID
     * @param store The merchant store
     * @param language The language for product details
     * @return ReadableWishlist with full product details
     * @throws Exception if operation fails
     */
    ReadableWishlist addToWishlist(Customer customer, PersistableWishlist wishlist,
            MerchantStore store, Language language) throws Exception;

    /**
     * Get all items in customer's wishlist.
     *
     * @param customer The customer
     * @param store The merchant store
     * @param language The language for product details
     * @return ReadableWishlistList with all wishlist items
     * @throws Exception if operation fails
     */
    ReadableWishlistList getWishlist(Customer customer, MerchantStore store,
            Language language) throws Exception;

    /**
     * Remove a product from customer's wishlist.
     *
     * @param customer The customer
     * @param productId The product ID to remove
     * @param store The merchant store
     * @throws Exception if operation fails
     */
    void removeFromWishlist(Customer customer, Long productId, MerchantStore store)
            throws Exception;

    /**
     * Check if a product exists in customer's wishlist.
     *
     * @param customer The customer
     * @param productId The product ID
     * @param store The merchant store
     * @return true if product is in wishlist, false otherwise
     * @throws Exception if operation fails
     */
    boolean existsInWishlist(Customer customer, Long productId, MerchantStore store)
            throws Exception;

    /**
     * Move a product from wishlist to shopping cart.
     * Adds product to cart with quantity 1 and removes from wishlist.
     *
     * @param customer The customer
     * @param productId The product ID
     * @param store The merchant store
     * @param language The language
     * @throws Exception if operation fails
     */
    void moveToCart(Customer customer, Long productId, MerchantStore store,
            Language language) throws Exception;

    /**
     * Get the count of items in customer's wishlist.
     *
     * @param customer The customer
     * @return Number of items in wishlist
     * @throws Exception if operation fails
     */
    Long getWishlistCount(Customer customer) throws Exception;
}
