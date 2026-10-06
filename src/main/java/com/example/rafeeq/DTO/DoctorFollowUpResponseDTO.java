package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorFollowUpResponseDTO {

    private Integer userId;
    private String followUpStatus;
    private String priority;
    private String summary;
    private List<FollowUpItemDTO> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FollowUpItemDTO {

        private String area;
        private String priority;
        private String latestReading;
        private String reason;
        private String recommendedAction;
    }
}