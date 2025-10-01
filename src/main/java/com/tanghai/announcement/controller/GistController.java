package com.tanghai.announcement.controller;

import com.tanghai.announcement.service.internet.GistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gist/cache")
public class GistController {

    private final GistService gistService;

    public GistController(GistService gistService) {
        this.gistService = gistService;
    }

    /** Clear Gist cache manually */
    @PostMapping("/clear")
    public ResponseEntity<String> clearCache() {
        gistService.clearCache();
        return ResponseEntity.ok("✅ Gist cache cleared successfully.");
    }

    /** Optional: check if cache exists */
    @GetMapping("/status")
    public ResponseEntity<String> cacheStatus() {
        boolean cached = gistService.isCacheAvailable();
        return ResponseEntity.ok("Cache exists: " + cached);
    }
}
