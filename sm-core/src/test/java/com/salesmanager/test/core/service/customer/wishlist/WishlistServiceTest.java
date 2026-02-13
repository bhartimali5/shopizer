package com.salesmanager.test.core.service.customer.wishlist;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.salesmanager.core.business.repositories.customer.wishlist.WishlistRepository;
import com.salesmanager.core.business.services.customer.wishlist.WishlistServiceImpl;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.wishlist.Wishlist;
import com.salesmanager.core.model.merchant.MerchantStore;

/**
 * Unit tests for WishlistService.
 * Tests all business logic operations for wishlist management.
 *
 * @author Shopizer Team
 */
@RunWith(MockitoJUnitRunner.class)
public class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    private Customer testCustomer;
    private Product testProduct;
    private MerchantStore testStore;
    private Wishlist testWishlist;

    @Before
    public void setUp() {
        // Setup test data
        testStore = new MerchantStore();
        testStore.setId(1);
        testStore.setCode("DEFAULT");

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setEmailAddress("test@test.com");
        testCustomer.setMerchantStore(testStore);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setSku("PROD-001");
        testProduct.setMerchantStore(testStore);

        testWishlist = new Wishlist();
        testWishlist.setId(1L);
        testWishlist.setCustomer(testCustomer);
        testWishlist.setProduct(testProduct);
        testWishlist.setDateAdded(new Date());
    }

    @Test
    public void testGetByCustomer_ReturnsWishlistItems() {
        // Given
        List<Wishlist> expectedWishlists = Arrays.asList(testWishlist);
        when(wishlistRepository.findByCustomer(testCustomer.getId()))
            .thenReturn(expectedWishlists);

        // When
        List<Wishlist> result = wishlistService.getByCustomer(testCustomer);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should return 1 wishlist item", 1, result.size());
        assertEquals("Should return correct wishlist", testWishlist, result.get(0));
        verify(wishlistRepository).findByCustomer(testCustomer.getId());
    }

    @Test
    public void testGetByCustomer_EmptyWishlist() {
        // Given
        when(wishlistRepository.findByCustomer(testCustomer.getId()))
            .thenReturn(Arrays.asList());

        // When
        List<Wishlist> result = wishlistService.getByCustomer(testCustomer);

        // Then
        assertNotNull("Result should not be null", result);
        assertTrue("Wishlist should be empty", result.isEmpty());
        verify(wishlistRepository).findByCustomer(testCustomer.getId());
    }

    @Test
    public void testGetByCustomerAndStore_ReturnsWishlistItems() {
        // Given
        List<Wishlist> expectedWishlists = Arrays.asList(testWishlist);
        when(wishlistRepository.findByCustomerAndStore(testCustomer.getId(), testStore.getId()))
            .thenReturn(expectedWishlists);

        // When
        List<Wishlist> result = wishlistService.getByCustomer(testCustomer, testStore);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should return 1 wishlist item", 1, result.size());
        verify(wishlistRepository).findByCustomerAndStore(testCustomer.getId(), testStore.getId());
    }

    @Test
    public void testGetByProduct_ReturnsCustomersWhoWishlisted() {
        // Given
        List<Wishlist> expectedWishlists = Arrays.asList(testWishlist);
        when(wishlistRepository.findByProduct(testProduct.getId()))
            .thenReturn(expectedWishlists);

        // When
        List<Wishlist> result = wishlistService.getByProduct(testProduct);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should return 1 wishlist item", 1, result.size());
        verify(wishlistRepository).findByProduct(testProduct.getId());
    }

    @Test
    public void testGetByCustomerAndProduct_ItemExists() {
        // Given
        when(wishlistRepository.findByCustomerAndProduct(testCustomer.getId(), testProduct.getId()))
            .thenReturn(testWishlist);

        // When
        Wishlist result = wishlistService.getByCustomerAndProduct(
            testCustomer.getId(), testProduct.getId());

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should return correct wishlist", testWishlist, result);
        verify(wishlistRepository).findByCustomerAndProduct(testCustomer.getId(), testProduct.getId());
    }

    @Test
    public void testGetByCustomerAndProduct_ItemDoesNotExist() {
        // Given
        when(wishlistRepository.findByCustomerAndProduct(testCustomer.getId(), testProduct.getId()))
            .thenReturn(null);

        // When
        Wishlist result = wishlistService.getByCustomerAndProduct(
            testCustomer.getId(), testProduct.getId());

        // Then
        assertNull("Result should be null when item doesn't exist", result);
        verify(wishlistRepository).findByCustomerAndProduct(testCustomer.getId(), testProduct.getId());
    }

    @Test
    public void testCountByCustomer_ReturnsCount() {
        // Given
        Long expectedCount = 5L;
        when(wishlistRepository.countByCustomer(testCustomer.getId()))
            .thenReturn(expectedCount);

        // When
        Long result = wishlistService.countByCustomer(testCustomer.getId());

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should return correct count", expectedCount, result);
        verify(wishlistRepository).countByCustomer(testCustomer.getId());
    }

    @Test
    public void testCountByCustomer_EmptyWishlist() {
        // Given
        when(wishlistRepository.countByCustomer(testCustomer.getId()))
            .thenReturn(0L);

        // When
        Long result = wishlistService.countByCustomer(testCustomer.getId());

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Count should be 0 for empty wishlist", Long.valueOf(0), result);
        verify(wishlistRepository).countByCustomer(testCustomer.getId());
    }

    @Test
    public void testExistsByCustomerAndProduct_ItemExists() {
        // Given
        when(wishlistRepository.findByCustomerAndProduct(testCustomer.getId(), testProduct.getId()))
            .thenReturn(testWishlist);

        // When
        boolean result = wishlistService.existsByCustomerAndProduct(
            testCustomer.getId(), testProduct.getId());

        // Then
        assertTrue("Should return true when item exists", result);
        verify(wishlistRepository).findByCustomerAndProduct(testCustomer.getId(), testProduct.getId());
    }

    @Test
    public void testExistsByCustomerAndProduct_ItemDoesNotExist() {
        // Given
        when(wishlistRepository.findByCustomerAndProduct(testCustomer.getId(), testProduct.getId()))
            .thenReturn(null);

        // When
        boolean result = wishlistService.existsByCustomerAndProduct(
            testCustomer.getId(), testProduct.getId());

        // Then
        assertFalse("Should return false when item doesn't exist", result);
        verify(wishlistRepository).findByCustomerAndProduct(testCustomer.getId(), testProduct.getId());
    }

    @Test
    public void testSaveWishlist_Success() throws Exception {
        // Given
        Wishlist newWishlist = new Wishlist();
        newWishlist.setCustomer(testCustomer);
        newWishlist.setProduct(testProduct);
        newWishlist.setDateAdded(new Date());

        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(newWishlist);

        // When
        wishlistService.save(newWishlist);

        // Then
        verify(wishlistRepository).save(newWishlist);
    }

    @Test
    public void testDeleteWishlist_Success() throws Exception {
        // When
        wishlistService.delete(testWishlist);

        // Then
        verify(wishlistRepository).delete(testWishlist);
    }

    @Test
    public void testMultipleProducts_CustomerHasMultipleWishlistItems() {
        // Given
        Product product2 = new Product();
        product2.setId(2L);
        product2.setSku("PROD-002");

        Wishlist wishlist2 = new Wishlist();
        wishlist2.setId(2L);
        wishlist2.setCustomer(testCustomer);
        wishlist2.setProduct(product2);

        List<Wishlist> expectedWishlists = Arrays.asList(testWishlist, wishlist2);
        when(wishlistRepository.findByCustomer(testCustomer.getId()))
            .thenReturn(expectedWishlists);

        // When
        List<Wishlist> result = wishlistService.getByCustomer(testCustomer);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("Should return 2 wishlist items", 2, result.size());
        assertTrue("Should contain first product", result.contains(testWishlist));
        assertTrue("Should contain second product", result.contains(wishlist2));
    }
}
