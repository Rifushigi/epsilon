package com.rifushigi.epsilon.service;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Service
public class UrlValidationService {

    private final String[] BLOCKED_DOMAINS = {
            "localhost", "127.0.0.1", "0.0.0.0", "::1"
    };

    private final UrlValidator urlValidator;

    public UrlValidationService(){
        String[] ALLOWED_PROTOCOLS = {"http", "https"};
        this.urlValidator = new UrlValidator(ALLOWED_PROTOCOLS);
    }

    public boolean isUrlValid(String url){
        if(url == null || url.trim().isEmpty()){
            return false;
        }

        if(!urlValidator.isValid(url)){
            if(!urlValidator.isValid("https://" + url)){
                return false;
            }
        }

        try{
            String urlToParse = url;

            if(!urlToParse.matches("^[a-zA-Z]+://.*")){
                urlToParse = "https://" + urlToParse;
            }

            URL parsedUrl = URI.create(urlToParse).toURL();

            if (isBlockedDomain(parsedUrl.getHost())) {
                return false;
            }

            } catch (IllegalArgumentException | MalformedURLException e) {
            return false;
        }
        return true;
    }


    private boolean isBlockedDomain(String host) {
        if (host == null) {
            return true;
        }

        String lowerHost = host.toLowerCase();
        for (String blockedDomain : BLOCKED_DOMAINS) {
            if (lowerHost.equals(blockedDomain) || lowerHost.endsWith("." + blockedDomain)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidCustomShortCode(String shortCode) {
        if (shortCode == null || shortCode.trim().isEmpty()) {
            return false;
        }

        if (shortCode.length() < 3 || shortCode.length() > 20) {
            return false;
        }

        return shortCode.matches("^[a-zA-Z0-9-]+$");
    }
}
