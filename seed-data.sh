#!/bin/bash

# Shopizer Data Seeding Script
# This script adds sample products via REST API

BASE_URL="http://localhost:8080/api/v2"
STORE="DEFAULT"
TOKEN=""

echo "🌱 Shopizer Data Seeding Script"
echo "================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check if server is running
echo -n "Checking if backend is running... "
if curl -s "${BASE_URL}/products?store=${STORE}" > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC}"
    echo "Please start the backend first: cd sm-shop && ../mvnw spring-boot:run"
    exit 1
fi

# Function to create product
create_product() {
    local sku=$1
    local name=$2
    local price=$3
    local quantity=$4
    local description=$5
    local highlights=$6
    local friendly_url=$7

    echo -n "Creating product: $name... "

    response=$(curl -s -w "\n%{http_code}" -X POST \
        "${BASE_URL}/private/product?store=${STORE}" \
        -H "Content-Type: application/json" \
        -d "{
            \"sku\": \"$sku\",
            \"available\": true,
            \"visible\": true,
            \"productSpecifications\": {
                \"height\": 0,
                \"weight\": 0,
                \"width\": 0,
                \"length\": 0
            },
            \"price\": $price,
            \"quantity\": $quantity,
            \"type\": {
                \"code\": \"general\"
            },
            \"descriptions\": [
                {
                    \"language\": \"en\",
                    \"name\": \"$name\",
                    \"friendlyUrl\": \"$friendly_url\",
                    \"description\": \"$description\",
                    \"highlights\": \"$highlights\"
                }
            ]
        }")

    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')

    if [ "$http_code" -eq 201 ]; then
        echo -e "${GREEN}✓${NC}"
        return 0
    else
        echo -e "${RED}✗ (HTTP $http_code)${NC}"
        echo "Response: $body"
        return 1
    fi
}

echo ""
echo "Creating sample products..."
echo ""

# Product 1: MacBook Pro
create_product \
    "LAPTOP-001" \
    "Apple MacBook Pro 14\" M3" \
    1999.99 \
    25 \
    "Powerful MacBook Pro with M3 chip delivers exceptional performance for professionals. Features stunning Liquid Retina XDR display, up to 22 hours battery life." \
    "M3 Pro chip • 14.2-inch display • 22 hours battery • 16GB RAM • 512GB SSD" \
    "macbook-pro-14-m3"

# Product 2: iPhone 15 Pro
create_product \
    "PHONE-001" \
    "iPhone 15 Pro 256GB" \
    1099.99 \
    50 \
    "iPhone 15 Pro features titanium design, powerful A17 Pro chip, and revolutionary 48MP camera system with 5x Telephoto zoom." \
    "Titanium design • A17 Pro chip • 48MP camera • 5x Telephoto • Action button" \
    "iphone-15-pro-256gb"

# Product 3: Samsung Galaxy S24
create_product \
    "PHONE-002" \
    "Samsung Galaxy S24 Ultra" \
    899.99 \
    40 \
    "Galaxy S24 Ultra with Galaxy AI on your side. Unleash creativity to communicate, work, and create like never before." \
    "6.8\" AMOLED • Snapdragon 8 Gen 3 • 200MP camera • S Pen included • 5000mAh battery" \
    "samsung-galaxy-s24-ultra"

# Product 4: Dell XPS 13
create_product \
    "LAPTOP-002" \
    "Dell XPS 13 Laptop" \
    1299.99 \
    30 \
    "Dell XPS 13 combines stunning design with powerful performance. Features Intel latest processors and beautiful InfinityEdge display." \
    "13.4\" FHD+ display • Intel Core i7 • 16GB RAM • 512GB SSD • 12 hours battery" \
    "dell-xps-13-laptop"

# Product 5: Apple Watch Series 9
create_product \
    "WATCH-001" \
    "Apple Watch Series 9 GPS 45mm" \
    429.99 \
    45 \
    "Apple Watch Series 9 features new S9 chip for improved performance and innovative double tap gesture. Track health and fitness with advanced sensors." \
    "S9 chip • Double tap gesture • Advanced health sensors • 18 hours battery • Always-On display" \
    "apple-watch-series-9-45mm"

# Product 6: Sony Headphones
create_product \
    "ACC-001" \
    "Sony WH-1000XM5 Wireless Headphones" \
    399.99 \
    60 \
    "Industry-leading noise cancellation meets exceptional sound quality. WH-1000XM5 headphones offer crystal-clear call quality and 30 hours battery life." \
    "Industry-leading ANC • 30-hour battery • Superior call quality • Lightweight • Multipoint connection" \
    "sony-wh-1000xm5-headphones"

# Product 7: iPad Pro
create_product \
    "TABLET-001" \
    "iPad Pro 11\" M2 Wi-Fi 256GB" \
    899.99 \
    35 \
    "iPad Pro features blazing-fast M2 chip and stunning Liquid Retina display. With Apple Pencil and Magic Keyboard support, ultimate creative tool." \
    "M2 chip • 11-inch Liquid Retina • ProMotion • Face ID • 12MP cameras • All-day battery" \
    "ipad-pro-11-m2-256gb"

# Product 8: Galaxy Buds
create_product \
    "ACC-002" \
    "Samsung Galaxy Buds2 Pro" \
    199.99 \
    70 \
    "Premium sound quality meets intelligent ANC. Galaxy Buds2 Pro deliver rich, balanced audio and intelligent noise cancellation." \
    "Intelligent ANC • 360 Audio • HD Voice • IPX7 water resistant • 8 hours playback" \
    "samsung-galaxy-buds2-pro"

# Product 9: Fitbit Charge 6
create_product \
    "WATCH-002" \
    "Fitbit Charge 6 Fitness Tracker" \
    159.99 \
    55 \
    "Track your health and fitness with Fitbit Charge 6. Features built-in GPS, heart rate monitoring, sleep tracking, and 7 days battery life." \
    "Built-in GPS • 24/7 heart rate • Sleep tracking • 40+ exercise modes • 7 days battery" \
    "fitbit-charge-6-tracker"

# Product 10: Anker PowerBank
create_product \
    "ACC-003" \
    "Anker PowerCore 20000mAh Power Bank" \
    49.99 \
    100 \
    "High-capacity portable charger with fast charging technology. Charge your phone, tablet, and devices multiple times with reliable power bank." \
    "20,000mAh capacity • Charges iPhone 4x • PowerIQ fast charging • Dual USB ports • Compact" \
    "anker-powercore-20000mah"

echo ""
echo "================================"
echo -e "${GREEN}✓ Data seeding complete!${NC}"
echo ""
echo "You can now:"
echo "  • View products in Swagger: http://localhost:8080/swagger-ui.html"
echo "  • Check API: GET /api/v1/products?store=DEFAULT"
echo "  • Browse shop UI (if running): http://localhost:3000"
echo ""
