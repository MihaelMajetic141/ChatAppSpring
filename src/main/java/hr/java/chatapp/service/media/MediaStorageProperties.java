package hr.java.chatapp.service.media;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@ConfigurationProperties(prefix = "app-media-storage")
@Component
public record MediaStorageProperties(
    String basePath,
    Set<String> allowedMimeTypes
) {
    public MediaStorageProperties() {
        this(
            "./media",
            Set.of("image/jpeg", "image/png", "image/gif", "image/webp")
        );
    }
}
