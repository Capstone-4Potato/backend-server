package com.potato.balbambalbam.main.cardFeedback.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter @Setter
@ToString
public class UserFeedbackResponseDto {

    private UserAudio userAudio;
    private Integer userScore;
    private Map<Long, String> recommendCard;
    private Waveform waveform;
    @Getter @Setter
     public static class UserAudio{
        private String text;

        private List<Integer> mistakenIndexes;

        public UserAudio(String text, List<Integer> mistakenIndexes) {
            this.text = text;
            this.mistakenIndexes = mistakenIndexes;
        }
    }

    @Getter @Setter
    public static class Waveform{
        private String userWaveform;
        private Double userAudioDuration;
        private String correctWaveform;
        private Double correctAudioDuration;

        public Waveform(String userWaveform, Double userAudioDuration, String correctWaveform, Double correctAudioDuration) {
            this.userWaveform = userWaveform;
            this.userAudioDuration = userAudioDuration;
            this.correctWaveform = correctWaveform;
            this.correctAudioDuration = correctAudioDuration;
        }
    }
}
