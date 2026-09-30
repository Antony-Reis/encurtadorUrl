package com.antony.encurtador.url;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class UrlService {
    private final IUrlRepository iUrlRepository;
    private final UrlProducer urlProducer;

    public UrlService(IUrlRepository iUrlRepository, UrlProducer urlProducer) {
        this.iUrlRepository = iUrlRepository;
        this.urlProducer = urlProducer;
    }

    private String converterUrlToHash(String urlLonga) throws NoSuchAlgorithmException {
        try {
            String salt = Instant.now().toString();

            String urlComSalt = urlLonga + salt;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(urlComSalt.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
            return hexString.substring(0, 6);

        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException(e);
        }
    }

    @Transactional
    public RUrlResponsePost postUrl(RUrlDto urlDto) throws NoSuchAlgorithmException {

        String urlConvertida = converterUrlToHash(urlDto.url());

        while (iUrlRepository.existsByUrlEncurtada(urlConvertida)){
            urlConvertida = converterUrlToHash(urlDto.url());
        }

        UrlEntity urlEntity = new UrlEntity(urlDto.url(), urlConvertida, EUrlStatusType.Ativa);
        iUrlRepository.save(urlEntity);
        return new RUrlResponsePost(HttpStatus.CREATED, urlConvertida);
    }

    @Transactional
    public String getUrl(String urlEncurtada) throws BadRequestException {
        UrlEntity urlEntity = iUrlRepository.findByUrlEncurtada(urlEncurtada)
                .orElseThrow(() -> new BadRequestException("Url encurtada não foi encontrada"));

        if (urlEntity.getStatus() == EUrlStatusType.Expirada) {
            throw new BadRequestException("Url expirada");
        }

        if (urlEntity.getCriadaQuando().isBefore(LocalDateTime.now().minusDays(10))) {
        urlEntity.setStatus(EUrlStatusType.Expirada);
        iUrlRepository.save(urlEntity);
        throw new BadRequestException("Url expirada");
        }

        RUrlEventAccessedDto event = new RUrlEventAccessedDto(
                urlEntity.getId(), LocalDateTime.now()
        );

        urlProducer.sendAccessedEvent(event);

        return urlEntity.getUrlOriginal();
    }
}
