package com.salesmanager.core.business.services.customer.wishlist;

import java.util.List;

import com.salesmanager.core.business.services.common.generic.SalesManagerEntityService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.wishlist.Wishlist;
import com.salesmanager.core.model.merchant.MerchantStore;

/**
 * Service interface for Wishlist operations.
 * Provides business logic for managing customer wishlists.
 *
 * @author Shopizer Team
 */
public interface WishlistService extends SalesManagerEntityService<Long, Wishlist> {

    /**
     * Get all wishlist items for a customer.
     *
     * @param customer The customer
     * @return List of wishlist items ordered by date added (most recent first)
     */
    List<Wishlist> getByCustomer(Customer customer);

    /**
     * Get wishlist items for a customer in a specific merchant store.
     * Supports multi-tenant architecture.
     *
     * @param customer The customer
     * @param store The merchant store
     * @return List of wishlist items ordered by date added (most recent first)
     */
    List<Wishlist> getByCustomer(Customer customer, MerchantStore store);

    /**
     * Get all customers who have wishlisted a specific product.
     *
     * @param product The product
     * @return List of wishlist items
     */
    List<Wishlist> getByProduct(Product product);

    /**
     * Check if a customer has wishlisted a specific product.
     *
     * @param customerId Customer ID
     * @param productId Product ID
     * @return Wishlist item or null if not found
     */
    Wishlist getByCustomerAndProduct(Long customerId, Long productId);

    /**
     * Count total wishlist items for a customer.
     *
     * @param customerId Customer ID
     * @return Number of items in customer's wishlist
     */
    Long countByCustomer(Long customerId);

    /**
     * Check if a product exists in customer's wishlist.
     *
     * @param customerId Customer ID
     * @param productId Product ID
     * @return true if product is in wishlist, false otherwise
     */
    boolean existsByCustomerAndProduct(Long customerId, Long productId);
}
