package com.antony.encurtador.url;

import com.antony.encurtador.exceptions.AuthErrorException;
import com.antony.encurtador.exceptions.MaxUrlPerUserErrorException;
import com.antony.encurtador.exceptions.NotFoundErrorException;
import com.antony.encurtador.exceptions.UrlExpiredErrorException;
import com.antony.encurtador.metrics.IMetricsRepository;
import com.antony.encurtador.url.utils.*;
import com.antony.encurtador.user.UserEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class UrlService {
    private final IUrlRepository iUrlRepository;
    private final UrlProducer urlProducer;
    private final RedisTemplate<String, RUrlCacheData> redisTemplate;
    private final IMetricsRepository iMetricsRepository;

    public UrlService(RedisTemplate<String, RUrlCacheData> redisTemplate, IUrlRepository iUrlRepository, UrlProducer urlProducer, IMetricsRepository iMetricsRepository) {
        this.redisTemplate = redisTemplate;
        this.iUrlRepository = iUrlRepository;
        this.urlProducer = urlProducer;
        this.iMetricsRepository = iMetricsRepository;
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
    public RUrlResponseDto postUrl(RUrlDto urlDto) throws NoSuchAlgorithmException, UrlExpiredErrorException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user =
                (UserEntity) authentication.getPrincipal();

        if (iUrlRepository.countByUsers(user) >= 10){
            throw new MaxUrlPerUserErrorException();
        }

        String urlConvertida = converterUrlToHash(urlDto.url());

        while (iUrlRepository.existsByUrlEncurtada(urlConvertida)){
            urlConvertida = converterUrlToHash(urlDto.url());
        }

        UrlEntity urlEntity = new UrlEntity(urlDto.url(), urlConvertida, EUrlStatusType.Ativa, user);
        iUrlRepository.save(urlEntity);

        RUrlCacheData rUrlCacheData = new RUrlCacheData(urlDto.url(), urlEntity.getId());
        redisTemplate.opsForValue().set(urlConvertida, rUrlCacheData, Duration.ofDays(10));

        return new RUrlResponseDto(HttpStatus.CREATED, urlConvertida);
    }

    @Transactional
    public String getUrl(String urlEncurtada) throws NotFoundErrorException {
        RUrlCacheData urlCacheData = redisTemplate.opsForValue().get(urlEncurtada);

        if (urlCacheData != null) {
            RUrlEventAccessedDto event = new RUrlEventAccessedDto(
                   urlCacheData.id(), LocalDateTime.now()
            );
            urlProducer.sendAccessedEvent(event);
            return urlCacheData.urlOriginal();
        }

        UrlEntity urlEntity = iUrlRepository.findByUrlEncurtada(urlEncurtada)
                    .orElseThrow(() -> new NotFoundErrorException("Url"));

        if (urlEntity.getStatus() == EUrlStatusType.Expirada) {
            throw new UrlExpiredErrorException();
        }

        if (urlEntity.getCriadaQuando().isBefore(LocalDateTime.now().minusDays(10))) {
        urlEntity.setStatus(EUrlStatusType.Expirada);
        iUrlRepository.save(urlEntity);
        throw new UrlExpiredErrorException();
        }

        RUrlEventAccessedDto event = new RUrlEventAccessedDto(
                urlEntity.getId(), LocalDateTime.now()
        );

        urlProducer.sendAccessedEvent(event);
        return urlEntity.getUrlOriginal();
    }

    private UserEntity getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return (UserEntity) authentication.getPrincipal();}

    @Transactional
    public RUrlResponseDto deleteUrl(String urlEncurtada) throws NotFoundErrorException, AuthErrorException{
        UrlEntity urlEntity = iUrlRepository.findByUrlEncurtada(urlEncurtada).orElseThrow(
                () -> new NotFoundErrorException("Url"));

        UserEntity user = getAuthenticatedUser();

        if (user.getId() != urlEntity.getUsers().getId()){
            throw new AuthErrorException();
        }
        iMetricsRepository.deleteAllByUrlId(urlEntity.getId());
        iUrlRepository.delete(urlEntity);

        return new RUrlResponseDto(HttpStatus.OK, "Url deletada com sucesso!");
    }
}
