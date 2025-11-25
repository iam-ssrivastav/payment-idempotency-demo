#!/bin/bash

echo "=== Idempotent Payments Demo Verification ==="
echo ""

KEY1="payment-$(date +%s)-001"
KEY2="payment-$(date +%s)-002"

echo "Test 1: First payment with key: $KEY1"
response1=$(curl -s -X POST http://localhost:8083/api/payments \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: $KEY1" \
  -d '{"amount": 100.50, "currency": "USD"}')
echo "Response: $response1"
payment_id=$(echo $response1 | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
echo ""

echo "Test 2: Duplicate payment with SAME key: $KEY1 (should return cached response)"
response2=$(curl -s -X POST http://localhost:8083/api/payments \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: $KEY1" \
  -d '{"amount": 100.50, "currency": "USD"}')
echo "Response: $response2"
echo ""

echo "Test 3: Same payment data but DIFFERENT key: $KEY2 (should create new payment)"
response3=$(curl -s -X POST http://localhost:8083/api/payments \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: $KEY2" \
  -d '{"amount": 100.50, "currency": "USD"}')
echo "Response: $response3"
echo ""

echo "Test 4: Payment without idempotency key (should fail with 400)"
response4=$(curl -s -w "\nHTTP Status: %{http_code}" -X POST http://localhost:8083/api/payments \
  -H "Content-Type: application/json" \
  -d '{"amount": 200.00, "currency": "EUR"}')
echo "Response: $response4"
echo ""

echo "Test 5: Get all payments"
curl -s http://localhost:8083/api/payments | python3 -m json.tool
echo ""

echo "=== Verification Complete ==="
echo ""
echo "Expected Results:"
echo "- Test 1: Creates payment with ID (e.g., id: 1)"
echo "- Test 2: Returns SAME payment ID (cached, no duplicate)"
echo "- Test 3: Creates NEW payment with different ID (e.g., id: 2)"
echo "- Test 4: Returns 400 Bad Request (missing idempotency key)"
echo "- Test 5: Shows 2 payments total"
