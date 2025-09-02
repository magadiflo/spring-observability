package dev.magadiflo.observability.app.controller;

import dev.magadiflo.observability.app.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping(path = "/api/v1/orders")
public class OrderController {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(new ArrayList<>(this.orders.values()));
    }

    @GetMapping(path = "/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        return Optional.ofNullable(this.orders.get(orderId))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order request) {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, request.product(), request.price(), request.quantity());
        this.orders.put(orderId, order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
