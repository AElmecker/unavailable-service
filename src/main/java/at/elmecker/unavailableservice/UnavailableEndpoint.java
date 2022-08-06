package at.elmecker.unavailableservice;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UnavailableEndpoint {

  private static final Logger LOG = LoggerFactory.getLogger(UnavailableEndpoint.class);

  private final MeterRegistry meterRegistry;

  public UnavailableEndpoint(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @RequestMapping("/**")
  ResponseEntity<Void> unavailable(HttpServletRequest request) {
    LOG.info("method {} for path {} is unavailable", request.getMethod(), request.getRequestURI());
    updateMetric(request);
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
  }

  private void updateMetric(HttpServletRequest request) {
    Counter.builder("unavailable.calls")
        .description("Count of calls to an unavailable endpoint, containing method and URI as dimensions.")
        .tags("method", request.getMethod(), "uri", request.getRequestURI())
        .register(meterRegistry)
        .increment();
  }
}
