package com.antony.encurtador.url;

import com.antony.encurtador.exceptions.AuthErrorException;
import com.antony.encurtador.exceptions.NotFoundErrorException;
import com.antony.encurtador.exceptions.UrlExpiredErrorException;
import com.antony.encurtador.url.utils.RUrlDto;
import com.antony.encurtador.url.utils.RUrlResponseDto;
import jakarta.validation.Valid;
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
    public RUrlResponseDto postUrl(@RequestBody @Valid RUrlDto body) throws NoSuchAlgorithmException, UrlExpiredErrorException {
        return urlService.postUrl(body);
    }

    @GetMapping("{urlEncurtada}")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<Void> getUrl(@PathVariable String urlEncurtada) throws NotFoundErrorException {
        String urlOriginal = urlService.getUrl(urlEncurtada);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(urlOriginal));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @DeleteMapping("{urlEcurtada}")
    public RUrlResponseDto deleteUrl(@PathVariable String urlEcurtada) throws NotFoundErrorException, AuthErrorException {
        return urlService.deleteUrl(urlEcurtada);
    }
}
