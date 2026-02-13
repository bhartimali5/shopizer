package com.salesmanager.shop.populator.customer;

import java.util.Date;

import org.apache.commons.lang3.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.customer.wishlist.Wishlist;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.wishlist.PersistableWishlist;

/**
 * Populator to convert PersistableWishlist (request DTO) to Wishlist entity.
 * Validates product belongs to the store before creating wishlist entry.
 *
 * @author Shopizer Team
 */
public class PersistableWishlistPopulator extends
        AbstractDataPopulator<PersistableWishlist, Wishlist> {

    private ProductService productService;

    @Override
    public Wishlist populate(PersistableWishlist source, Wishlist target,
            MerchantStore store, Language language) throws ConversionException {

        Validate.notNull(productService, "productService cannot be null");
        Validate.notNull(source.getProductId(), "Product ID cannot be null");

        try {
            if (target == null) {
                target = new Wishlist();
            }

            // Get and validate product
            Product product = productService.getById(source.getProductId());

            if (product == null) {
                throw new ConversionException("Product with id [" + source.getProductId() + "] not found");
            }

            // Validate product belongs to store
            if (product.getMerchantStore().getId().intValue() != store.getId().intValue()) {
                throw new ConversionException(
                    "Invalid product id [" + source.getProductId() + "] for the given store [" + store.getCode() + "]");
            }

            target.setProduct(product);
            target.setDateAdded(new Date());

            return target;

        } catch (Exception e) {
            throw new ConversionException("Cannot populate Wishlist", e);
        }
    }

    @Override
    protected Wishlist createTarget() {
        return null;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
