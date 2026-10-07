package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyTimelineItemDTO {

    private String time;
    private String type;
    private String title;
    private String details;
}