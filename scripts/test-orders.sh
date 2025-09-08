#!/bin/bash

# Lista de productos disponibles
products=("Mouse" "Teclado" "Monitor" "Laptop" "Auriculares")

for i in {1..100} ; do
  # Seleccionar producto aleatorio
  product=${products[$RANDOM % ${#products[@]}]}

  # Precio aleatorio entre 10 y 500 (con dos decimales)
  price=$(awk -v min=10 -v max=500 'BEGIN{srand(); printf "%.2f", min+rand()*(max-min)}')

  # Cantidad aleatoria entre 1 y 5
  quantity=$(( (RANDOM % 5) + 1 ))

  curl -s -X POST -H "Content-type: application/json" \
    -d "{\"product\": \"${product}\", \"price\": ${price}, \"quantity\": ${quantity}}" \
     http://192.168.1.7:8080/api/v1/orders \
     > /dev/null

  echo "[${i}] Orden enviada: ${product} - ${price} PEN - Cantidad: ${quantity}"
done
