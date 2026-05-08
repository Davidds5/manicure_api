package br.com.davidds5.manicure_api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {


        @GetMapping("/test-log")
        public String testLog() {
            log.trace("TRACE log");
            log.debug("DEBUG log");
            log.info("INFO log");
            log.warn("WARN log");
            log.error("ERROR log");
            return "Logs funcionando!";
        }
    }

