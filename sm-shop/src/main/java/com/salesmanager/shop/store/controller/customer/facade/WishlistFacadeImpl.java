package com.salesmanager.shop.store.controller.customer.facade;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.pricing.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.customer.wishlist.WishlistService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.wishlist.Wishlist;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.wishlist.PersistableWishlist;
import com.salesmanager.shop.model.customer.wishlist.ReadableWishlist;
import com.salesmanager.shop.model.customer.wishlist.ReadableWishlistList;
import com.salesmanager.shop.model.shoppingcart.PersistableShoppingCartItem;
import com.salesmanager.shop.populator.customer.PersistableWishlistPopulator;
import com.salesmanager.shop.populator.customer.ReadableWishlistPopulator;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.controller.shoppingCart.facade.ShoppingCartFacade;
import com.salesmanager.shop.utils.ImageFilePath;

/**
 * Implementation of WishlistFacade providing business orchestration for wishlist operations.
 *
 * @author Shopizer Team
 */
@Service("wishlistFacade")
public class WishlistFacadeImpl implements WishlistFacade {

    @Inject
    private WishlistService wishlistService;

    @Inject
    private ProductService productService;

    @Inject
    private ShoppingCartFacade shoppingCartFacade;

    @Inject
    private PricingService pricingService;

    @Inject
    @Qualifier("img")
    private ImageFilePath imageUtils;

    @Override
    public ReadableWishlist addToWishlist(Customer customer, PersistableWishlist wishlist,
            MerchantStore store, Language language) throws Exception {

        Validate.notNull(customer, "Customer cannot be null");
        Validate.notNull(wishlist, "Wishlist cannot be null");
        Validate.notNull(store, "MerchantStore cannot be null");

        // Check if already exists
        Wishlist existingWishlist = wishlistService.getByCustomerAndProduct(
            customer.getId(), wishlist.getProductId());

        if (existingWishlist != null) {
            // Already in wishlist, return existing
            return convertToReadable(existingWishlist, store, language);
        }

        // Create new wishlist entry
        Wishlist wishlistModel = new Wishlist();

        // Setup populator
        PersistableWishlistPopulator populator = new PersistableWishlistPopulator();
        populator.setProductService(productService);
        populator.populate(wishlist, wishlistModel, store, language);

        wishlistModel.setCustomer(customer);

        // Save to database
        wishlistService.save(wishlistModel);

        // Convert to readable
        return convertToReadable(wishlistModel, store, language);
    }

    @Override
    public ReadableWishlistList getWishlist(Customer customer, MerchantStore store,
            Language language) throws Exception {

        Validate.notNull(customer, "Customer cannot be null");
        Validate.notNull(store, "MerchantStore cannot be null");

        List<Wishlist> wishlistItems = wishlistService.getByCustomer(customer, store);

        ReadableWishlistList readableList = new ReadableWishlistList();
        List<ReadableWishlist> readableItems = new ArrayList<>();

        for (Wishlist item : wishlistItems) {
            ReadableWishlist readable = convertToReadable(item, store, language);
            readableItems.add(readable);
        }

        readableList.setWishlists(readableItems);
        readableList.setTotal((long) readableItems.size());

        return readableList;
    }

    @Override
    public void removeFromWishlist(Customer customer, Long productId,
            MerchantStore store) throws Exception {

        Validate.notNull(customer, "Customer cannot be null");
        Validate.notNull(productId, "Product ID cannot be null");

        Wishlist wishlist = wishlistService.getByCustomerAndProduct(
            customer.getId(), productId);

        if (wishlist == null) {
            throw new ResourceNotFoundException(
                "Wishlist item not found for customer [" + customer.getId()
                + "] and product [" + productId + "]");
        }

        // Validate store
        if (wishlist.getProduct().getMerchantStore().getId().intValue()
                != store.getId().intValue()) {
            throw new ServiceException(
                "Invalid store for this wishlist item. Product belongs to store ["
                + wishlist.getProduct().getMerchantStore().getCode()
                + "] but request is for store [" + store.getCode() + "]");
        }

        wishlistService.delete(wishlist);
    }

    @Override
    public boolean existsInWishlist(Customer customer, Long productId,
            MerchantStore store) throws Exception {

        Validate.notNull(customer, "Customer cannot be null");
        Validate.notNull(productId, "Product ID cannot be null");

        return wishlistService.existsByCustomerAndProduct(customer.getId(), productId);
    }

    @Override
    public void moveToCart(Customer customer, Long productId, MerchantStore store,
            Language language) throws Exception {

        Validate.notNull(customer, "Customer cannot be null");
        Validate.notNull(productId, "Product ID cannot be null");

        // Get wishlist item
        Wishlist wishlist = wishlistService.getByCustomerAndProduct(
            customer.getId(), productId);

        if (wishlist == null) {
            throw new ResourceNotFoundException(
                "Wishlist item not found for customer [" + customer.getId()
                + "] and product [" + productId + "]");
        }

        // Get product
        Product product = wishlist.getProduct();

        // Add to cart using shopping cart facade
        PersistableShoppingCartItem cartItem = new PersistableShoppingCartItem();
        cartItem.setProduct(product.getSku());
        cartItem.setQuantity(1);

        shoppingCartFacade.addToCart(cartItem, store, language);

        // Remove from wishlist
        wishlistService.delete(wishlist);
    }

    @Override
    public Long getWishlistCount(Customer customer) throws Exception {
        Validate.notNull(customer, "Customer cannot be null");
        return wishlistService.countByCustomer(customer.getId());
    }

    /**
     * Helper method to convert Wishlist entity to ReadableWishlist DTO.
     */
    private ReadableWishlist convertToReadable(Wishlist wishlist, MerchantStore store,
            Language language) throws Exception {

        ReadableWishlist readable = new ReadableWishlist();

        // Create and configure ReadableProductPopulator with required dependencies
        com.salesmanager.shop.populator.catalog.ReadableProductPopulator productPopulator =
            new com.salesmanager.shop.populator.catalog.ReadableProductPopulator();
        productPopulator.setPricingService(pricingService);
        productPopulator.setimageUtils(imageUtils);

        // Create and configure ReadableWishlistPopulator
        ReadableWishlistPopulator populator = new ReadableWishlistPopulator();
        populator.setReadableProductPopulator(productPopulator);
        populator.populate(wishlist, readable, store, language);

        return readable;
    }
}
