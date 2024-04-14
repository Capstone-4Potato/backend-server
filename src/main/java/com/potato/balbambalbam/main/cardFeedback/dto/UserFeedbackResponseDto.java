package com.potato.balbambalbam.main.cardFeedback.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class UserFeedbackResponseDto {

    private UserAudio userAudio;
    private UserScore userScore;
    private RecommendCard recommendCard;
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

    public static class UserScore{
        private Integer currentScore;
        private Integer highestScore;

        public UserScore(Integer currentScore, Integer highestScore) {
            this.currentScore = currentScore;
            this.highestScore = highestScore;
        }
    }
    @Getter @Setter
    public static class RecommendCard{
        private List<Character> pronunciation;
        private List<Long> ids;

        public RecommendCard(List<Character> pronunciation, List<Long> ids) {
            this.pronunciation = pronunciation;
            this.ids = ids;
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
