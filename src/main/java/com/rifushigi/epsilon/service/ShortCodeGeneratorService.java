package com.rifushigi.epsilon.service;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

@Service
public class ShortCodeGeneratorService {

    private static final int DEFAULT_LENGTH = 7;
    private final RandomStringGenerator randomStringGenerator;

    public ShortCodeGeneratorService(){
        this.randomStringGenerator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(LETTERS, DIGITS)
                .usingRandom(new SecureRandom()::nextInt)
                .get();
    }

    public String generateShortCode(){
        return randomStringGenerator.generate(DEFAULT_LENGTH);
    }

    public String generateShortCode(int length){
        return randomStringGenerator.generate(length);
    }
}
