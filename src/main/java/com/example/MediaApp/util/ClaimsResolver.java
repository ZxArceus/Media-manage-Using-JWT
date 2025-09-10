package com.example.MediaApp.util;

import io.jsonwebtoken.Claims;
@FunctionalInterface
public interface ClaimsResolver <T>{
    T resolve(Claims claims);
}
