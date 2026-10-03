package com.antony.encurtador.metrics;

import com.antony.encurtador.exceptions.AuthErrorException;
import com.antony.encurtador.exceptions.NotFoundErrorException;
import com.antony.encurtador.metrics.utils.RMetricsResponseDto;
import com.antony.encurtador.metrics.utils.RUrlResponseDto;
import com.antony.encurtador.url.IUrlRepository;
import com.antony.encurtador.url.UrlEntity;
import com.antony.encurtador.user.UserEntity;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {
    private final IUrlRepository iUrlRepository;
    private final IMetricsRepository iMetricsRepository;

    public MetricsService(IUrlRepository iUrlRepository, IMetricsRepository iMetricsRepository) {
        this.iUrlRepository = iUrlRepository;
        this.iMetricsRepository = iMetricsRepository;
    }

    private UserEntity getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return (UserEntity) authentication.getPrincipal();}

    public Page<RUrlResponseDto> getUrlsPage(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("criadaQuando").ascending());
        UserEntity user = getAuthenticatedUser();
        Page<UrlEntity> urls = iUrlRepository.findByUsers(user, pageable);
        return urls.map(RUrlResponseDto::new);
    }

    public Page<RMetricsResponseDto> getMetricsUrlPage(String urlEncurtada,Integer page, Integer size) throws NotFoundErrorException, AuthErrorException {
        UrlEntity urlEntity = iUrlRepository.findByUrlEncurtada(urlEncurtada).orElseThrow(
                () -> new NotFoundErrorException("Url"));

        UserEntity user = getAuthenticatedUser();

        if (user.getId() != urlEntity.getUsers().getId()){
            throw new AuthErrorException();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("acessadaQuando").ascending());
        Page<MetricsEntity> metrics = iMetricsRepository.findByUrlId(urlEntity.getId(), pageable);

        return metrics.map(RMetricsResponseDto::new);
        }

    public long getNumberAcessesUrl(String urlEncurtada) throws NotFoundErrorException, AuthErrorException {
        UrlEntity urlEntity = iUrlRepository.findByUrlEncurtada(urlEncurtada).orElseThrow(
                () -> new NotFoundErrorException("Url"));

        UserEntity user = getAuthenticatedUser();

        if (user.getId() != urlEntity.getUsers().getId()){
            throw new AuthErrorException();
        }
        return iMetricsRepository.countByUrlId(urlEntity.getId());
    }

    }

