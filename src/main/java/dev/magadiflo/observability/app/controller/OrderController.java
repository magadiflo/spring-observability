package dev.magadiflo.observability.app.controller;

import dev.magadiflo.observability.app.model.Order;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/orders")
public class OrderController {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final Counter orderCreatedCounter;

    public OrderController(MeterRegistry registry) {
        // Creamos un Counter personalizado
        this.orderCreatedCounter = Counter.builder("orders_total")
                .description("Total de órdenes creadas")
                .register(registry);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("Obteniendo todas las órdenes. Total actual: {}", this.orders.size());
        return ResponseEntity.ok(new ArrayList<>(this.orders.values()));
    }

    @GetMapping(path = "/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        log.info("Buscando orden con ID: {}", orderId);

        return Optional.ofNullable(this.orders.get(orderId))
                .map(order -> {
                    log.debug("Orden encontrada: {}", order);
                    return ResponseEntity.ok(order);
                })
                .orElseGet(() -> {
                    log.warn("Orden con ID: {} no encontrada", orderId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order request) {
        try {
            // Validación simple para generar diferentes tipos de logs
            if (request.price().compareTo(BigDecimal.ZERO) <= 0) {
                log.error("Error: Precio inválido recibido: {}", request.price());
                return ResponseEntity.badRequest().build();
            }

            String orderId = UUID.randomUUID().toString();
            Order order = new Order(orderId, request.product(), request.price(), request.quantity());
            this.orders.put(orderId, order);
            log.info("Nueva orden creada: {}", order);

            // Incrementa el contador cada vez que se crea una orden
            log.info("Incrementando el counter");
            this.orderCreatedCounter.increment();

            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            log.error("Error inesperado al crear orden: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
