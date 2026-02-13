package com.salesmanager.core.business.repositories.customer.wishlist;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.salesmanager.core.model.customer.wishlist.Wishlist;

/**
 * Repository for Wishlist entity with custom queries optimized for performance.
 * Uses JOIN FETCH to prevent N+1 query problems.
 *
 * @author Shopizer Team
 */
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    /**
     * Find wishlist item by ID with eager loading of related entities.
     * Uses JOIN FETCH to avoid N+1 query problem.
     *
     * @param id Wishlist ID
     * @return Wishlist with customer, product, and merchant store loaded
     */
    @Query("select w from Wishlist w "
            + "join fetch w.customer wc "
            + "join fetch w.product wp "
            + "join fetch wp.merchantStore wpm "
            + "where w.id = ?1")
    Wishlist findOne(Long id);

    /**
     * Find all wishlist items for a customer, ordered by most recent first.
     * Eagerly loads product descriptions for display.
     *
     * @param customerId Customer ID
     * @return List of wishlist items ordered by dateAdded DESC
     */
    @Query("select w from Wishlist w "
            + "join fetch w.customer wc "
            + "join fetch w.product wp "
            + "join fetch wp.merchantStore wpm "
            + "left join fetch wp.descriptions wpd "
            + "where wc.id = ?1 "
            + "order by w.dateAdded desc")
    List<Wishlist> findByCustomer(Long customerId);

    /**
     * Find all customers who have wishlisted a specific product.
     *
     * @param productId Product ID
     * @return List of wishlist items
     */
    @Query("select w from Wishlist w "
            + "join fetch w.customer wc "
            + "join fetch w.product wp "
            + "join fetch wp.merchantStore wpm "
            + "where wp.id = ?1")
    List<Wishlist> findByProduct(Long productId);

    /**
     * Find wishlist item for a specific customer and product combination.
     * Used for duplicate checking before adding to wishlist.
     *
     * @param customerId Customer ID
     * @param productId Product ID
     * @return Wishlist item or null if not found
     */
    @Query("select w from Wishlist w "
            + "join fetch w.customer wc "
            + "join fetch w.product wp "
            + "join fetch wp.merchantStore wpm "
            + "where wc.id = ?1 and wp.id = ?2")
    Wishlist findByCustomerAndProduct(Long customerId, Long productId);

    /**
     * Find wishlist items for a customer in a specific merchant store.
     * Supports multi-tenant architecture.
     *
     * @param customerId Customer ID
     * @param storeId Merchant Store ID
     * @return List of wishlist items ordered by dateAdded DESC
     */
    @Query("select w from Wishlist w "
            + "join fetch w.customer wc "
            + "join fetch w.product wp "
            + "join fetch wp.merchantStore wpm "
            + "where wc.id = ?1 and wpm.id = ?2 "
            + "order by w.dateAdded desc")
    List<Wishlist> findByCustomerAndStore(Long customerId, Integer storeId);

    /**
     * Count total wishlist items for a customer.
     *
     * @param customerId Customer ID
     * @return Number of items in wishlist
     */
    @Query("select count(w) from Wishlist w where w.customer.id = ?1")
    Long countByCustomer(Long customerId);
}
