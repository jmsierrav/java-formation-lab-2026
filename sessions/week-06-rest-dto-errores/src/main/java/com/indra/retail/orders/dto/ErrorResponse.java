package com.indra.retail.orders.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(LocalDateTime timestamp, int status, List<String> errors) {
}
