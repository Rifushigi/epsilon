package com.rifushigi.epsilon.service;

import com.rifushigi.epsilon.dao.ClickRepository;
import com.rifushigi.epsilon.entity.Click;
import com.rifushigi.epsilon.entity.ShortUrl;
import org.springframework.stereotype.Service;

@Service
public class ClickService {

    private final ClickRepository clickRepository;

    public ClickService(ClickRepository clickRepository) {
        this.clickRepository = clickRepository;
    }

    public void saveClickDetails(ShortUrl shortUrl, String ipAddress, String userAgent, String referer) {
        Click click = new Click();
        click.setShortUrl(shortUrl);
        click.setIpAddress(ipAddress);
        click.setUserAgent(userAgent);
        click.setReferer(referer);

        clickRepository.save(click);
    }
}
