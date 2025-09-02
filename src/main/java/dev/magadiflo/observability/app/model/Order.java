package dev.magadiflo.observability.app.model;

import java.math.BigDecimal;

public record Order(String orderId, String product, BigDecimal price, int quantity) {
}
