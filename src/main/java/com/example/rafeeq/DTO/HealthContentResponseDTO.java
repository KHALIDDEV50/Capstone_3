package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthContentResponseDTO {

    private List<HealthArticleDTO> articles;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HealthArticleDTO {

        private String title;
        private String category;
        private String summary;
        private String source;
        private LocalDate publishedAt;
        private String url;
    }
}