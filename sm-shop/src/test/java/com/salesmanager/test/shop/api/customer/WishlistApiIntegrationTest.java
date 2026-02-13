package com.salesmanager.test.shop.api.customer;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.salesmanager.shop.application.ShopApplication;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.model.customer.wishlist.PersistableWishlist;
import com.salesmanager.shop.model.customer.wishlist.ReadableWishlist;
import com.salesmanager.shop.model.customer.wishlist.ReadableWishlistList;
import com.salesmanager.test.shop.common.ServicesTestSupport;

/**
 * Integration tests for Wishlist REST API.
 * Tests complete flow from HTTP request to response including authentication.
 *
 * @author Shopizer Team
 */
@SpringBootTest(classes = ShopApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class WishlistApiIntegrationTest extends ServicesTestSupport {

    private ReadableProduct testProduct;

    @BeforeEach
    public void setUp() throws Exception {
        // Create a test product using the existing helper method
        testProduct = sampleProduct("wishlist-test-product");
        assertNotNull("Test product should be created", testProduct);
    }

    @Test
    public void testAddToWishlist_Success() throws Exception {
        // Given
        PersistableWishlist request = new PersistableWishlist();
        request.setProductId(testProduct.getId());

        // When
        final HttpEntity<PersistableWishlist> entity = new HttpEntity<>(request, getHeader());
        final ResponseEntity<ReadableWishlist> response = testRestTemplate.postForEntity(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                entity,
                ReadableWishlist.class);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusCode(), is(CREATED));
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(testProduct.getId(), response.getBody().getProduct().getId());
        assertNotNull(response.getBody().getDateAdded());
    }

    @Test
    public void testAddToWishlist_DuplicateReturnsExisting() throws Exception {
        // Given - Add product first time
        PersistableWishlist request = new PersistableWishlist();
        request.setProductId(testProduct.getId());

        final HttpEntity<PersistableWishlist> entity = new HttpEntity<>(request, getHeader());
        final ResponseEntity<ReadableWishlist> firstResponse = testRestTemplate.postForEntity(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                entity,
                ReadableWishlist.class);

        assertThat(firstResponse.getStatusCode(), is(CREATED));
        Long firstWishlistId = firstResponse.getBody().getId();

        // When - Add same product again
        final ResponseEntity<ReadableWishlist> secondResponse = testRestTemplate.postForEntity(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                entity,
                ReadableWishlist.class);

        // Then - Should return same wishlist item (same ID)
        assertThat(secondResponse.getStatusCode(), is(CREATED));
        assertEquals("Should return same wishlist item for duplicate add",
            firstWishlistId, secondResponse.getBody().getId());
    }

    @Test
    public void testAddToWishlist_Unauthenticated_Returns401() throws Exception {
        // Given
        PersistableWishlist request = new PersistableWishlist();
        request.setProductId(testProduct.getId());

        // When & Then - No authentication token
        final HttpEntity<PersistableWishlist> entity = new HttpEntity<>(request);
        final ResponseEntity<ReadableWishlist> response = testRestTemplate.postForEntity(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                entity,
                ReadableWishlist.class);

        assertThat(response.getStatusCode(), is(UNAUTHORIZED));
    }

    @Test
    public void testAddToWishlist_InvalidProductId_Returns404() throws Exception {
        // Given
        PersistableWishlist request = new PersistableWishlist();
        request.setProductId(99999L); // Non-existent product

        // When & Then
        final HttpEntity<PersistableWishlist> entity = new HttpEntity<>(request, getHeader());
        final ResponseEntity<ReadableWishlist> response = testRestTemplate.postForEntity(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                entity,
                ReadableWishlist.class);

        assertThat(response.getStatusCode(), is(NOT_FOUND));
    }

    @Test
    public void testGetWishlist_Success() throws Exception {
        // Given - Add product to wishlist first
        PersistableWishlist request = new PersistableWishlist();
        request.setProductId(testProduct.getId());

        final HttpEntity<PersistableWishlist> addEntity = new HttpEntity<>(request, getHeader());
        testRestTemplate.postForEntity(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                addEntity,
                ReadableWishlist.class);

        // When
        final HttpEntity<String> getEntity = new HttpEntity<>(getHeader());
        final ResponseEntity<ReadableWishlistList> response = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                HttpMethod.GET,
                getEntity,
                ReadableWishlistList.class);

        // Then
        assertThat(response.getStatusCode(), is(OK));
        assertNotNull(response.getBody());
        assertEquals(Long.valueOf(1), response.getBody().getTotal());
        assertNotNull(response.getBody().getWishlists());
        assertEquals(1, response.getBody().getWishlists().size());
        assertEquals(testProduct.getId(), response.getBody().getWishlists().get(0).getProduct().getId());
    }

    @Test
    public void testGetWishlist_EmptyWishlist() throws Exception {
        // When - Customer has no wishlist items
        final HttpEntity<String> getEntity = new HttpEntity<>(getHeader());
        final ResponseEntity<ReadableWishlistList> response = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                HttpMethod.GET,
                getEntity,
                ReadableWishlistList.class);

        // Then
        assertThat(response.getStatusCode(), is(OK));
        assertNotNull(response.getBody());
        assertEquals(Long.valueOf(0), response.getBody().getTotal());
        assertTrue(response.getBody().getWishlists().isEmpty());
    }

    @Test
    public void testGetWishlist_Unauthenticated_Returns401() throws Exception {
        // When & Then - No authentication
        final HttpEntity<String> getEntity = new HttpEntity<>(null);
        final ResponseEntity<ReadableWishlistList> response = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                HttpMethod.GET,
                getEntity,
                ReadableWishlistList.class);

        assertThat(response.getStatusCode(), is(UNAUTHORIZED));
    }

    @Test
    public void testGetWishlistCount_Success() throws Exception {
        // Given - Add product to wishlist
        PersistableWishlist request = new PersistableWishlist();
        request.setProductId(testProduct.getId());

        final HttpEntity<PersistableWishlist> addEntity = new HttpEntity<>(request, getHeader());
        testRestTemplate.postForEntity(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                addEntity,
                ReadableWishlist.class);

        // When
        final HttpEntity<String> getEntity = new HttpEntity<>(getHeader());
        final ResponseEntity<Long> response = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/count?store=DEFAULT",
                HttpMethod.GET,
                getEntity,
                Long.class);

        // Then
        assertThat(response.getStatusCode(), is(OK));
        assertEquals(Long.valueOf(1), response.getBody());
    }

    @Test
    public void testGetWishlistCount_EmptyWishlist() throws Exception {
        // When
        final HttpEntity<String> getEntity = new HttpEntity<>(getHeader());
        final ResponseEntity<Long> response = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/count?store=DEFAULT",
                HttpMethod.GET,
                getEntity,
                Long.class);

        // Then
        assertThat(response.getStatusCode(), is(OK));
        assertEquals(Long.valueOf(0), response.getBody());
    }

    @Test
    public void testExistsInWishlist_ProductExists() throws Exception {
        // Given - Add product to wishlist
        PersistableWishlist request = new PersistableWishlist();
        request.setProductId(testProduct.getId());

        final HttpEntity<PersistableWishlist> addEntity = new HttpEntity<>(request, getHeader());
        testRestTemplate.postForEntity(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                addEntity,
                ReadableWishlist.class);

        // When
        final HttpEntity<String> getEntity = new HttpEntity<>(getHeader());
        final ResponseEntity<Boolean> response = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/product/" + testProduct.getId() + "/exists?store=DEFAULT",
                HttpMethod.GET,
                getEntity,
                Boolean.class);

        // Then
        assertThat(response.getStatusCode(), is(OK));
        assertEquals(Boolean.TRUE, response.getBody());
    }

    @Test
    public void testExistsInWishlist_ProductDoesNotExist() throws Exception {
        // When - Product not in wishlist
        final HttpEntity<String> getEntity = new HttpEntity<>(getHeader());
        final ResponseEntity<Boolean> response = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/product/" + testProduct.getId() + "/exists?store=DEFAULT",
                HttpMethod.GET,
                getEntity,
                Boolean.class);

        // Then
        assertThat(response.getStatusCode(), is(OK));
        assertEquals(Boolean.FALSE, response.getBody());
    }

    @Test
    public void testRemoveFromWishlist_Success() throws Exception {
        // Given - Add product to wishlist first
        PersistableWishlist request = new PersistableWishlist();
        request.setProductId(testProduct.getId());

        final HttpEntity<PersistableWishlist> addEntity = new HttpEntity<>(request, getHeader());
        testRestTemplate.postForEntity(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                addEntity,
                ReadableWishlist.class);

        // When - Remove from wishlist
        final HttpEntity<String> deleteEntity = new HttpEntity<>(getHeader());
        final ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/product/" + testProduct.getId() + "?store=DEFAULT",
                HttpMethod.DELETE,
                deleteEntity,
                Void.class);

        // Then
        assertThat(response.getStatusCode(), is(NO_CONTENT));

        // Verify it's removed
        final ResponseEntity<Long> countResponse = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/count?store=DEFAULT",
                HttpMethod.GET,
                deleteEntity,
                Long.class);

        assertEquals(Long.valueOf(0), countResponse.getBody());
    }

    @Test
    public void testRemoveFromWishlist_ProductNotInWishlist_Returns404() throws Exception {
        // When & Then - Try to remove product that's not in wishlist
        final HttpEntity<String> deleteEntity = new HttpEntity<>(getHeader());
        final ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/product/" + testProduct.getId() + "?store=DEFAULT",
                HttpMethod.DELETE,
                deleteEntity,
                Void.class);

        assertThat(response.getStatusCode(), is(NOT_FOUND));
    }

    @Test
    public void testRemoveFromWishlist_Unauthenticated_Returns401() throws Exception {
        // When & Then - No authentication
        final HttpEntity<String> deleteEntity = new HttpEntity<>(null);
        final ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/product/" + testProduct.getId() + "?store=DEFAULT",
                HttpMethod.DELETE,
                deleteEntity,
                Void.class);

        assertThat(response.getStatusCode(), is(UNAUTHORIZED));
    }

    @Test
    public void testMoveToCart_Success() throws Exception {
        // Given - Add product to wishlist
        PersistableWishlist request = new PersistableWishlist();
        request.setProductId(testProduct.getId());

        final HttpEntity<PersistableWishlist> addEntity = new HttpEntity<>(request, getHeader());
        testRestTemplate.postForEntity(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                addEntity,
                ReadableWishlist.class);

        // When - Move to cart
        final HttpEntity<String> moveEntity = new HttpEntity<>(getHeader());
        final ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/product/" + testProduct.getId() + "/movetocart?store=DEFAULT&lang=en",
                HttpMethod.POST,
                moveEntity,
                Void.class);

        // Then
        assertThat(response.getStatusCode(), is(OK));

        // Verify it's removed from wishlist
        final ResponseEntity<Long> countResponse = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/count?store=DEFAULT",
                HttpMethod.GET,
                moveEntity,
                Long.class);

        assertEquals(Long.valueOf(0), countResponse.getBody());
    }

    @Test
    public void testMoveToCart_ProductNotInWishlist_Returns404() throws Exception {
        // When & Then - Try to move product not in wishlist
        final HttpEntity<String> moveEntity = new HttpEntity<>(getHeader());
        final ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/product/" + testProduct.getId() + "/movetocart?store=DEFAULT&lang=en",
                HttpMethod.POST,
                moveEntity,
                Void.class);

        assertThat(response.getStatusCode(), is(NOT_FOUND));
    }

    @Test
    public void testCompleteWorkflow_AddViewRemove() throws Exception {
        final HttpEntity<String> getEntity = new HttpEntity<>(getHeader());

        // 1. Verify empty wishlist
        ResponseEntity<Long> countResponse = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/count?store=DEFAULT",
                HttpMethod.GET,
                getEntity,
                Long.class);
        assertThat(countResponse.getStatusCode(), is(OK));
        assertEquals(Long.valueOf(0), countResponse.getBody());

        // 2. Add product to wishlist
        PersistableWishlist request = new PersistableWishlist();
        request.setProductId(testProduct.getId());

        final HttpEntity<PersistableWishlist> addEntity = new HttpEntity<>(request, getHeader());
        ResponseEntity<ReadableWishlist> addResponse = testRestTemplate.postForEntity(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                addEntity,
                ReadableWishlist.class);
        assertThat(addResponse.getStatusCode(), is(CREATED));

        // 3. Verify count is 1
        countResponse = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/count?store=DEFAULT",
                HttpMethod.GET,
                getEntity,
                Long.class);
        assertEquals(Long.valueOf(1), countResponse.getBody());

        // 4. Verify product exists
        ResponseEntity<Boolean> existsResponse = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/product/" + testProduct.getId() + "/exists?store=DEFAULT",
                HttpMethod.GET,
                getEntity,
                Boolean.class);
        assertEquals(Boolean.TRUE, existsResponse.getBody());

        // 5. Get wishlist
        ResponseEntity<ReadableWishlistList> listResponse = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist?store=DEFAULT&lang=en",
                HttpMethod.GET,
                getEntity,
                ReadableWishlistList.class);
        assertThat(listResponse.getStatusCode(), is(OK));
        assertEquals(Long.valueOf(1), listResponse.getBody().getTotal());

        // 6. Remove from wishlist
        ResponseEntity<Void> deleteResponse = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/product/" + testProduct.getId() + "?store=DEFAULT",
                HttpMethod.DELETE,
                getEntity,
                Void.class);
        assertThat(deleteResponse.getStatusCode(), is(NO_CONTENT));

        // 7. Verify count is 0
        countResponse = testRestTemplate.exchange(
                "/api/v1/auth/customer/wishlist/count?store=DEFAULT",
                HttpMethod.GET,
                getEntity,
                Long.class);
        assertEquals(Long.valueOf(0), countResponse.getBody());
    }
}
