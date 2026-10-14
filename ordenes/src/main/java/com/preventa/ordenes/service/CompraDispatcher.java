package com.preventa.ordenes.service;

import com.preventa.ordenes.model.*;
import com.preventa.ordenes.repository.CompraPendienteRepository;
import java.time.Instant;
import org.slf4j.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class CompraDispatcher {
  private static final Logger log = LoggerFactory.getLogger(CompraDispatcher.class);
  private final CompraPendienteRepository jobs;
  private final OrdenService ordenes;
  private final ObjectProvider<SqsClient> sqs;
  private final String mode, url;

  public CompraDispatcher(
      CompraPendienteRepository jobs,
      OrdenService ordenes,
      ObjectProvider<SqsClient> sqs,
      @Value("${eventpass.queue.mode}") String mode,
      @Value("${eventpass.queue.url}") String url) {
    this.jobs = jobs;
    this.ordenes = ordenes;
    this.sqs = sqs;
    this.mode = mode;
    this.url = url;
    if (!java.util.Set.of("manual", "local", "sqs").contains(mode))
      throw new IllegalArgumentException("QUEUE_MODE debe ser manual, local o sqs");
    if (mode.equals("sqs") && url.isBlank())
      throw new IllegalArgumentException("SQS_QUEUE_URL requerido");
  }

  @Scheduled(fixedDelayString = "${eventpass.queue.poll-ms:5000}")
  public void dispatch() {
    if (mode.equals("manual")) return;
    for (CompraPendiente job :
        jobs.findTop20ByEnviadaFalseAndSiguienteIntentoBeforeOrderByOrdenIdAsc(Instant.now())) {
      try {
        if (ordenes.get(job.getOrdenId()).estado() != Orden.Estado.PENDIENTE) {
          job.setEnviada(true);
        } else if (mode.equals("local")) {
          ordenes.procesar(job.getOrdenId());
          job.setEnviada(true);
        } else {
          SendMessageRequest.Builder request =
              SendMessageRequest.builder()
                  .queueUrl(url)
                  .messageBody(Long.toString(job.getOrdenId()));
          if (url.endsWith(".fifo"))
            request
                .messageGroupId("compras")
                .messageDeduplicationId(Long.toString(job.getOrdenId()));
          sqs.getObject().sendMessage(request.build());
          job.setEnviada(true);
        }
        job.setUltimoError(null);
      } catch (Exception ex) {
        job.setIntentos(job.getIntentos() + 1);
        job.setUltimoError(ex.getClass().getSimpleName());
        job.setSiguienteIntento(Instant.now().plusSeconds(Math.min(300, 5L * job.getIntentos())));
        log.warn("Compra {} pendiente; intento {}", job.getOrdenId(), job.getIntentos());
      }
      jobs.save(job);
    }
  }
}
