package com.preventa.lambda;

import com.amazonaws.services.lambda.runtime.*;
import com.amazonaws.services.lambda.runtime.events.*;
import java.net.*;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

/**
 * Handler: com.preventa.lambda.CompraHandler::handleRequest. Requiere ORDENES_URL e INTERNAL_KEY.
 * Activar ReportBatchItemFailures en el trigger SQS.
 */
public class CompraHandler implements RequestHandler<SQSEvent, SQSBatchResponse> {
  private final HttpClient client =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

  private static String env(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) throw new IllegalStateException(name + " requerido");
    return value;
  }

  public SQSBatchResponse handleRequest(SQSEvent event, Context context) {
    List<SQSBatchResponse.BatchItemFailure> failures = new ArrayList<>();
    boolean fifoBlocked = false;
    for (SQSEvent.SQSMessage message : event.getRecords()) {
      boolean fifo =
          message.getEventSourceArn() != null && message.getEventSourceArn().endsWith(".fifo");
      if (fifoBlocked && fifo) {
        failures.add(new SQSBatchResponse.BatchItemFailure(message.getMessageId()));
        continue;
      }
      try {
        long id = Long.parseLong(message.getBody().trim());
        if (id <= 0) throw new IllegalArgumentException("ID invalido");
        String base = env("ORDENES_URL").replaceAll("/$", "");
        HttpRequest request =
            HttpRequest.newBuilder(URI.create(base + "/internal/ordenes/" + id + "/procesar"))
                .timeout(Duration.ofSeconds(25))
                .header("X-Internal-Key", env("INTERNAL_KEY"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        if (response.statusCode() < 200 || response.statusCode() >= 300)
          throw new IllegalStateException("HTTP " + response.statusCode());
      } catch (Exception ex) {
        if (ex instanceof InterruptedException) Thread.currentThread().interrupt();
        context
            .getLogger()
            .log("Fallo mensaje " + message.getMessageId() + ": " + ex.getClass().getSimpleName());
        failures.add(new SQSBatchResponse.BatchItemFailure(message.getMessageId()));
        if (fifo) fifoBlocked = true;
      }
    }
    return new SQSBatchResponse(failures);
  }
}
