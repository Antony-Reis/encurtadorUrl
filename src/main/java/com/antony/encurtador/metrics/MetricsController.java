package com.antony.encurtador.metrics;

import com.antony.encurtador.exceptions.AuthErrorException;
import com.antony.encurtador.exceptions.NotFoundErrorException;
import com.antony.encurtador.metrics.utils.RMetricsResponseDto;
import com.antony.encurtador.metrics.utils.RUrlResponseDto;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/metrics")
public class MetricsController {
    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping()
    public Page<RUrlResponseDto> getUrlsPage(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size){
        return metricsService.getUrlsPage(page, size);
    }


    @GetMapping("/{urlEncurtada}")
    public Page<RMetricsResponseDto> getMetricsUrlPage(@PathVariable String urlEncurtada,
                                                       @RequestParam(defaultValue = "0") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer size) throws NotFoundErrorException, AuthErrorException {
        return metricsService.getMetricsUrlPage(urlEncurtada, page, size);
    }

    @GetMapping("/count/{urlEncurtada}")
    public long getNumberAcessesUrl(@PathVariable String urlEncurtada) throws NotFoundErrorException, AuthErrorException {
        return metricsService.getNumberAcessesUrl(urlEncurtada);
    }
}
