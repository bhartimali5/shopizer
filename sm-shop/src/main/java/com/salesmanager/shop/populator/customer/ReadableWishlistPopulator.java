package com.salesmanager.shop.populator.customer;

import org.apache.commons.lang3.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.wishlist.Wishlist;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.model.customer.wishlist.ReadableWishlist;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import com.salesmanager.shop.utils.DateUtil;

/**
 * Populator to convert Wishlist entity to ReadableWishlist (response DTO).
 * Includes full product details for display.
 *
 * @author Shopizer Team
 */
public class ReadableWishlistPopulator extends
        AbstractDataPopulator<Wishlist, ReadableWishlist> {

    private ReadableProductPopulator readableProductPopulator;

    @Override
    public ReadableWishlist populate(Wishlist source, ReadableWishlist target,
            MerchantStore store, Language language) throws ConversionException {

        Validate.notNull(readableProductPopulator, "readableProductPopulator cannot be null");

        try {
            if (target == null) {
                target = new ReadableWishlist();
            }

            target.setId(source.getId());
            target.setCustomerId(source.getCustomer().getId());

            // Populate full product details
            ReadableProduct readableProduct = new ReadableProduct();
            readableProductPopulator.populate(source.getProduct(), readableProduct, store, language);
            target.setProduct(readableProduct);

            // Format date
            if (source.getDateAdded() != null) {
                target.setDateAdded(DateUtil.formatDate(source.getDateAdded()));
            }

            return target;

        } catch (Exception e) {
            throw new ConversionException("Cannot populate ReadableWishlist", e);
        }
    }

    @Override
    protected ReadableWishlist createTarget() {
        return null;
    }

    public ReadableProductPopulator getReadableProductPopulator() {
        return readableProductPopulator;
    }

    public void setReadableProductPopulator(ReadableProductPopulator readableProductPopulator) {
        this.readableProductPopulator = readableProductPopulator;
    }
}
