package com.antony.encurtador.url;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/v1/url")
@Validated
public class UrlController {
    UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public RUrlResponsePost postUrl(@RequestBody @Valid RUrlDto body) throws BadRequestException, NoSuchAlgorithmException {
        return urlService.postUrl(body);
    }

    @GetMapping("{urlEncurtada}")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<Void> getUrl(@PathVariable String urlEncurtada) throws BadRequestException {
        String urlOriginal = urlService.getUrl(urlEncurtada);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(urlOriginal));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}
