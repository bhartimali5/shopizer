package com.salesmanager.core.business.services.customer.wishlist;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.salesmanager.core.business.repositories.customer.wishlist.WishlistRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.wishlist.Wishlist;
import com.salesmanager.core.model.merchant.MerchantStore;

/**
 * Service implementation for Wishlist operations.
 *
 * @author Shopizer Team
 */
@Service("wishlistService")
public class WishlistServiceImpl extends SalesManagerEntityServiceImpl<Long, Wishlist>
        implements WishlistService {

    private final WishlistRepository wishlistRepository;

    @Inject
    public WishlistServiceImpl(WishlistRepository wishlistRepository) {
        super(wishlistRepository);
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public List<Wishlist> getByCustomer(Customer customer) {
        return wishlistRepository.findByCustomer(customer.getId());
    }

    @Override
    public List<Wishlist> getByCustomer(Customer customer, MerchantStore store) {
        return wishlistRepository.findByCustomerAndStore(customer.getId(), store.getId());
    }

    @Override
    public List<Wishlist> getByProduct(Product product) {
        return wishlistRepository.findByProduct(product.getId());
    }

    @Override
    public Wishlist getByCustomerAndProduct(Long customerId, Long productId) {
        return wishlistRepository.findByCustomerAndProduct(customerId, productId);
    }

    @Override
    public Long countByCustomer(Long customerId) {
        return wishlistRepository.countByCustomer(customerId);
    }

    @Override
    public boolean existsByCustomerAndProduct(Long customerId, Long productId) {
        return getByCustomerAndProduct(customerId, productId) != null;
    }
}
