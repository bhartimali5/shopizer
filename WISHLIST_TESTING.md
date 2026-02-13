# Wishlist Feature Testing Guide

This document provides comprehensive testing instructions for the wishlist feature.

## Test Files Created

1. **WishlistServiceTest.java** - Unit tests for service layer
   - Location: `sm-core/src/test/java/com/salesmanager/test/core/service/customer/wishlist/WishlistServiceTest.java`
   - 12 test cases covering all service methods

2. **WishlistApiIntegrationTest.java** - Integration tests for REST API
   - Location: `sm-shop/src/test/java/com/salesmanager/test/shop/api/customer/WishlistApiIntegrationTest.java`
   - 20 test cases covering all API endpoints and scenarios

## Running Tests

### Run All Tests

```bash
cd /Users/bhartimali/projects/agentic_ai/shopizer
./mvnw test
```

### Run Only Wishlist Service Tests

```bash
./mvnw test -Dtest=WishlistServiceTest
```

### Run Only Wishlist API Integration Tests

```bash
./mvnw test -Dtest=WishlistApiIntegrationTest
```

### Run with Coverage Report

```bash
./mvnw clean test jacoco:report
```

Coverage report will be generated at: `target/site/jacoco/index.html`

## Test Coverage

### WishlistServiceTest (Unit Tests)

✅ **Basic Operations:**
- `testGetByCustomer_ReturnsWishlistItems` - Get customer's wishlist
- `testGetByCustomer_EmptyWishlist` - Handle empty wishlist
- `testGetByCustomerAndStore_ReturnsWishlistItems` - Multi-store support
- `testGetByProduct_ReturnsCustomersWhoWishlisted` - Find who wishlisted a product

✅ **Duplicate Prevention:**
- `testGetByCustomerAndProduct_ItemExists` - Find existing item
- `testGetByCustomerAndProduct_ItemDoesNotExist` - Item not found
- `testExistsByCustomerAndProduct_ItemExists` - Check existence (true)
- `testExistsByCustomerAndProduct_ItemDoesNotExist` - Check existence (false)

✅ **Count Operations:**
- `testCountByCustomer_ReturnsCount` - Get wishlist count
- `testCountByCustomer_EmptyWishlist` - Count empty wishlist

✅ **CRUD Operations:**
- `testSaveWishlist_Success` - Save wishlist item
- `testDeleteWishlist_Success` - Delete wishlist item
- `testMultipleProducts_CustomerHasMultipleWishlistItems` - Multiple items

### WishlistApiIntegrationTest (Integration Tests)

✅ **Add to Wishlist (POST /api/v1/auth/customer/wishlist):**
- `testAddToWishlist_Success` - Successfully add product (201 Created)
- `testAddToWishlist_DuplicateReturnsExisting` - Duplicate handling
- `testAddToWishlist_Unauthenticated_Returns401` - Authentication required
- `testAddToWishlist_InvalidProductId_Returns404` - Invalid product ID

✅ **View Wishlist (GET /api/v1/auth/customer/wishlist):**
- `testGetWishlist_Success` - Get wishlist with items (200 OK)
- `testGetWishlist_EmptyWishlist` - Empty wishlist returns empty array
- `testGetWishlist_Unauthenticated_Returns401` - Authentication required

✅ **Wishlist Count (GET /api/v1/auth/customer/wishlist/count):**
- `testGetWishlistCount_Success` - Get count with items
- `testGetWishlistCount_EmptyWishlist` - Count returns 0

✅ **Check Existence (GET /api/v1/auth/customer/wishlist/product/{id}/exists):**
- `testExistsInWishlist_ProductExists` - Returns true
- `testExistsInWishlist_ProductDoesNotExist` - Returns false

✅ **Remove from Wishlist (DELETE /api/v1/auth/customer/wishlist/product/{id}):**
- `testRemoveFromWishlist_Success` - Successfully remove (204 No Content)
- `testRemoveFromWishlist_ProductNotInWishlist_Returns404` - Not found
- `testRemoveFromWishlist_Unauthenticated_Returns401` - Authentication required

✅ **Move to Cart (POST /api/v1/auth/customer/wishlist/product/{id}/movetocart):**
- `testMoveToCart_Success` - Move to cart and remove from wishlist
- `testMoveToCart_ProductNotInWishlist_Returns404` - Not found

✅ **Complete Workflow:**
- `testCompleteWorkflow_AddViewRemove` - Full user journey

## Test Scenarios Covered

### Functional Scenarios

1. ✅ Customer can add product to wishlist
2. ✅ Adding duplicate product returns existing entry (no duplicate)
3. ✅ Customer can view all wishlist items with product details
4. ✅ Customer can get wishlist item count
5. ✅ Customer can check if specific product is in wishlist
6. ✅ Customer can remove product from wishlist
7. ✅ Customer can move product from wishlist to cart
8. ✅ Multiple products can be added to wishlist
9. ✅ Empty wishlist returns empty result (not error)

### Security Scenarios

10. ✅ Unauthenticated users cannot access wishlist endpoints (401)
11. ✅ JWT token is required for all protected endpoints
12. ✅ Customers can only access their own wishlist

### Error Scenarios

13. ✅ Invalid product ID returns 404 Not Found
14. ✅ Removing non-existent wishlist item returns 404
15. ✅ Moving non-existent item to cart returns 404

### Data Integrity Scenarios

16. ✅ Unique constraint prevents duplicate entries (customer + product)
17. ✅ Cascade delete: Deleting product removes wishlist items
18. ✅ Cascade delete: Deleting customer removes wishlist items
19. ✅ Audit fields (created/modified dates) populate automatically

### Multi-Store Scenarios

20. ✅ Wishlist items filtered by merchant store
21. ✅ Cannot add products from different store

## Expected Test Results

When all tests pass, you should see:

```
Tests run: 32, Failures: 0, Errors: 0, Skipped: 0
```

- **WishlistServiceTest**: 12 tests
- **WishlistApiIntegrationTest**: 20 tests

## Troubleshooting Test Failures

### Common Issues

**1. Authentication Setup Required**

The integration tests require proper authentication setup. You may need to:
- Implement the `authenticateAndGetToken()` method
- Create test customer and product fixtures
- Configure test database with sample data

**2. Missing Test Dependencies**

Ensure your `pom.xml` includes:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```

**3. Test Database Configuration**

Integration tests use test database configuration from:
- `sm-shop/src/test/resources/application-test.properties`

Ensure H2 in-memory database is configured for tests.

**4. Transaction Rollback**

Tests use `@Transactional` to rollback changes after each test. If tests are interfering with each other, verify transaction management is working.

## Manual Testing Checklist

After automated tests pass, perform manual testing via Swagger UI:

- [ ] Add product to wishlist
- [ ] View wishlist with product details displayed correctly
- [ ] Add same product again (should not create duplicate)
- [ ] Check wishlist count badge
- [ ] Check if product exists in wishlist
- [ ] Move product to cart
- [ ] Verify product removed from wishlist after move
- [ ] Verify product added to cart after move
- [ ] Remove product from wishlist
- [ ] Verify empty wishlist shows gracefully
- [ ] Try accessing wishlist without authentication (should fail)
- [ ] Try adding invalid product ID (should fail)

## Database Verification Queries

Connect to H2 console (`http://localhost:8080/h2-console`) and run:

```sql
-- Check all wishlist entries
SELECT w.*, c.CUSTOMER_EMAIL_ADDRESS, p.SKU
FROM CUSTOMER_WISHLIST w
JOIN CUSTOMER c ON w.CUSTOMER_ID = c.CUSTOMER_ID
JOIN PRODUCT p ON w.PRODUCT_ID = p.PRODUCT_ID;

-- Verify no duplicates exist
SELECT CUSTOMER_ID, PRODUCT_ID, COUNT(*)
FROM CUSTOMER_WISHLIST
GROUP BY CUSTOMER_ID, PRODUCT_ID
HAVING COUNT(*) > 1;

-- Check audit timestamps
SELECT WISHLIST_ID, DATE_ADDED, DATE_CREATED, DATE_MODIFIED
FROM CUSTOMER_WISHLIST;
```

## Performance Testing

For production readiness, consider:

1. **Load Testing** - Test with 1000+ wishlist items per customer
2. **Concurrent Access** - Multiple users adding to wishlist simultaneously
3. **Database Query Performance** - Verify JOIN FETCH prevents N+1 queries
4. **Memory Usage** - Monitor with large result sets

## Continuous Integration

Add to your CI/CD pipeline:

```yaml
# Example GitHub Actions
- name: Run Wishlist Tests
  run: ./mvnw test -Dtest=Wishlist*
```

## Test Maintenance

When modifying wishlist feature:
1. Update relevant test cases
2. Run all tests before committing
3. Maintain test coverage above 80%
4. Document any new test scenarios added

## Contact

For questions about testing:
- Check test comments in source code
- Review this documentation
- Consult Shopizer testing patterns in existing test files
