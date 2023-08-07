package com.itm.space.backendresources.controller;

import com.itm.space.backendresources.exception.BackendResourcesException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.NotBlank;

@RestController
@Validated
public class TestController {

    @GetMapping("/causeBackendException")
    public void causeBackendException() {
        throw new BackendResourcesException("Тест исключений", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/causeMethodArgumentNotValidException")
    public void causeMethodArgumentNotValidException(@NotBlank String input) {
        throw new BackendResourcesException("Тест исключений", HttpStatus.BAD_REQUEST);
// Этот метод предназначен для выброса исключения MethodArgumentNotValidException,
// потому что у нас есть ограничение @NotBlank на входной параметр,
// но мы не предоставляем способа передать этот параметр.
    }
}