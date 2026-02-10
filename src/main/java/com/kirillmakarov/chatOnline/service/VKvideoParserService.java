package com.kirillmakarov.chatOnline.service;


import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VKvideoParserService {


    private final static Pattern VK_PATTERN = Pattern.compile("video(-?\\d+)_(\\d+)");

    public static String extractIds(String url){
        Matcher matcher = VK_PATTERN.matcher(url);
        if (matcher.find()){

            return matcher.group(1) + "_" + matcher.group(2);

        }
        return null;
    };


}
