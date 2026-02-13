package com.salesmanager.shop.store.api.v1.customer;

import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.wishlist.PersistableWishlist;
import com.salesmanager.shop.model.customer.wishlist.ReadableWishlist;
import com.salesmanager.shop.model.customer.wishlist.ReadableWishlistList;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.store.controller.customer.facade.WishlistFacade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import springfox.documentation.annotations.ApiIgnore;

/**
 * REST API for customer wishlist management.
 * All endpoints require JWT authentication.
 *
 * @author Shopizer Team
 */
@Controller
@RequestMapping("/api/v1")
@Api(tags = { "Customer wishlist management" })
@SwaggerDefinition(tags = {
    @Tag(name = "Customer wishlist", description = "Add, view and remove wishlist items")
})
public class WishlistApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(WishlistApi.class);

    @Inject
    private WishlistFacade wishlistFacade;

    @Inject
    private CustomerFacade customerFacade;

    /**
     * Add product to authenticated customer's wishlist.
     * If product already exists in wishlist, returns the existing entry.
     *
     * POST /api/v1/auth/customer/wishlist
     */
    @PostMapping("/auth/customer/wishlist")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(
        httpMethod = "POST",
        value = "Add product to wishlist",
        notes = "Adds a product to the authenticated customer's wishlist. Returns existing entry if product is already in wishlist.",
        produces = "application/json",
        response = ReadableWishlist.class
    )
    @ApiImplicitParams({
        @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
        @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
    })
    public @ResponseBody ReadableWishlist addToWishlist(
            @Valid @RequestBody PersistableWishlist wishlist,
            @ApiIgnore MerchantStore merchantStore,
            @ApiIgnore Language language,
            HttpServletRequest request) {

        try {
            Principal principal = request.getUserPrincipal();

            if (principal == null) {
                throw new UnauthorizedException("User must be authenticated to add items to wishlist");
            }

            // Get customer
            Customer customer = customerFacade.getCustomerByUserName(
                principal.getName(), merchantStore);

            if (customer == null) {
                throw new ResourceNotFoundException(
                    "Customer not found for principal: " + principal.getName());
            }

            // Add to wishlist
            return wishlistFacade.addToWishlist(customer, wishlist, merchantStore, language);

        } catch (Exception e) {
            LOGGER.error("Error adding product to wishlist", e);
            if (e instanceof UnauthorizedException) {
                throw (UnauthorizedException) e;
            } else if (e instanceof ResourceNotFoundException) {
                throw (ResourceNotFoundException) e;
            } else {
                throw new ServiceRuntimeException(e);
            }
        }
    }

    /**
     * Get authenticated customer's wishlist.
     *
     * GET /api/v1/auth/customer/wishlist
     */
    @GetMapping("/auth/customer/wishlist")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        httpMethod = "GET",
        value = "Get customer wishlist",
        notes = "Retrieves all items in the authenticated customer's wishlist with full product details",
        produces = "application/json",
        response = ReadableWishlistList.class
    )
    @ApiImplicitParams({
        @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
        @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
    })
    public @ResponseBody ReadableWishlistList getWishlist(
            @ApiIgnore MerchantStore merchantStore,
            @ApiIgnore Language language,
            HttpServletRequest request) {

        try {
            Principal principal = request.getUserPrincipal();

            if (principal == null) {
                throw new UnauthorizedException("User must be authenticated to view wishlist");
            }

            Customer customer = customerFacade.getCustomerByUserName(
                principal.getName(), merchantStore);

            if (customer == null) {
                throw new ResourceNotFoundException(
                    "Customer not found for principal: " + principal.getName());
            }

            return wishlistFacade.getWishlist(customer, merchantStore, language);

        } catch (Exception e) {
            LOGGER.error("Error getting wishlist", e);
            if (e instanceof UnauthorizedException) {
                throw (UnauthorizedException) e;
            } else if (e instanceof ResourceNotFoundException) {
                throw (ResourceNotFoundException) e;
            } else {
                throw new ServiceRuntimeException(e);
            }
        }
    }

    /**
     * Remove product from wishlist.
     *
     * DELETE /api/v1/auth/customer/wishlist/product/{productId}
     */
    @DeleteMapping("/auth/customer/wishlist/product/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(
        httpMethod = "DELETE",
        value = "Remove product from wishlist",
        notes = "Removes a specific product from the authenticated customer's wishlist"
    )
    @ApiImplicitParams({
        @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT")
    })
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long productId,
            @ApiIgnore MerchantStore merchantStore,
            HttpServletRequest request) {

        try {
            Principal principal = request.getUserPrincipal();

            if (principal == null) {
                throw new UnauthorizedException("User must be authenticated to remove items from wishlist");
            }

            Customer customer = customerFacade.getCustomerByUserName(
                principal.getName(), merchantStore);

            if (customer == null) {
                throw new ResourceNotFoundException(
                    "Customer not found for principal: " + principal.getName());
            }

            wishlistFacade.removeFromWishlist(customer, productId, merchantStore);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            LOGGER.error("Error removing product from wishlist", e);
            if (e instanceof UnauthorizedException) {
                throw (UnauthorizedException) e;
            } else if (e instanceof ResourceNotFoundException) {
                throw (ResourceNotFoundException) e;
            } else {
                throw new ServiceRuntimeException(e);
            }
        }
    }

    /**
     * Check if product is in wishlist.
     *
     * GET /api/v1/auth/customer/wishlist/product/{productId}/exists
     */
    @GetMapping("/auth/customer/wishlist/product/{productId}/exists")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        httpMethod = "GET",
        value = "Check if product is in wishlist",
        notes = "Returns true if the product is in customer's wishlist, false otherwise",
        produces = "application/json",
        response = Boolean.class
    )
    @ApiImplicitParams({
        @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT")
    })
    public @ResponseBody Boolean existsInWishlist(
            @PathVariable Long productId,
            @ApiIgnore MerchantStore merchantStore,
            HttpServletRequest request) {

        try {
            Principal principal = request.getUserPrincipal();

            if (principal == null) {
                return false;
            }

            Customer customer = customerFacade.getCustomerByUserName(
                principal.getName(), merchantStore);

            if (customer == null) {
                return false;
            }

            return wishlistFacade.existsInWishlist(customer, productId, merchantStore);

        } catch (Exception e) {
            LOGGER.error("Error checking if product is in wishlist", e);
            return false;
        }
    }

    /**
     * Move item from wishlist to cart.
     *
     * POST /api/v1/auth/customer/wishlist/product/{productId}/movetocart
     */
    @PostMapping("/auth/customer/wishlist/product/{productId}/movetocart")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        httpMethod = "POST",
        value = "Move wishlist item to cart",
        notes = "Adds the product to shopping cart with quantity 1 and removes it from wishlist"
    )
    @ApiImplicitParams({
        @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
        @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
    })
    public ResponseEntity<Void> moveToCart(
            @PathVariable Long productId,
            @ApiIgnore MerchantStore merchantStore,
            @ApiIgnore Language language,
            HttpServletRequest request) {

        try {
            Principal principal = request.getUserPrincipal();

            if (principal == null) {
                throw new UnauthorizedException("User must be authenticated to move items to cart");
            }

            Customer customer = customerFacade.getCustomerByUserName(
                principal.getName(), merchantStore);

            if (customer == null) {
                throw new ResourceNotFoundException(
                    "Customer not found for principal: " + principal.getName());
            }

            wishlistFacade.moveToCart(customer, productId, merchantStore, language);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            LOGGER.error("Error moving product to cart", e);
            if (e instanceof UnauthorizedException) {
                throw (UnauthorizedException) e;
            } else if (e instanceof ResourceNotFoundException) {
                throw (ResourceNotFoundException) e;
            } else {
                throw new ServiceRuntimeException(e);
            }
        }
    }

    /**
     * Get wishlist item count.
     *
     * GET /api/v1/auth/customer/wishlist/count
     */
    @GetMapping("/auth/customer/wishlist/count")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        httpMethod = "GET",
        value = "Get wishlist count",
        notes = "Returns the number of items in customer's wishlist",
        produces = "application/json",
        response = Long.class
    )
    @ApiImplicitParams({
        @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT")
    })
    public @ResponseBody Long getWishlistCount(
            @ApiIgnore MerchantStore merchantStore,
            HttpServletRequest request) {

        try {
            Principal principal = request.getUserPrincipal();

            if (principal == null) {
                return 0L;
            }

            Customer customer = customerFacade.getCustomerByUserName(
                principal.getName(), merchantStore);

            if (customer == null) {
                return 0L;
            }

            return wishlistFacade.getWishlistCount(customer);

        } catch (Exception e) {
            LOGGER.error("Error getting wishlist count", e);
            return 0L;
        }
    }
}
