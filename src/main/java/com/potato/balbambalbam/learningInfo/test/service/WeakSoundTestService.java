package com.potato.balbambalbam.learningInfo.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.data.entity.UserWeakSound;
import com.potato.balbambalbam.data.entity.WeakSoundTest;
import com.potato.balbambalbam.data.entity.WeakSoundTestStatus;
import com.potato.balbambalbam.data.repository.UserWeakSoundRepository;
import com.potato.balbambalbam.data.repository.WeakSoundTestRepository;
import com.potato.balbambalbam.data.repository.WeakSoundTestSatusRepositoy;
import com.potato.balbambalbam.exception.ResponseNotFoundException;
import com.potato.balbambalbam.learningInfo.test.dto.WeakSoundTestListDto;
import com.potato.balbambalbam.learningInfo.test.dto.TestResponseDto;
import com.potato.balbambalbam.learningInfo.weaksound.service.PhonemeService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
public class WeakSoundTestService {
    private final WeakSoundTestRepository weakSoundTestRepository;
    private final ObjectMapper objectMapper; // json형식 변환시 사용
    private final WeakSoundTestAiService weakSoundTestAiService;
    private final PhonemeService phonemeService;
    private final UserWeakSoundRepository userWeakSoundRepository;
    private final WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy;


    public WeakSoundTestService(WeakSoundTestRepository weakSoundTestRepository,
                                ObjectMapper objectMapper,
                                WeakSoundTestAiService weakSoundTestAiService,
                                PhonemeService phonemeService,
                                UserWeakSoundRepository userWeakSoundRepository,
                                WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy) {
        this.weakSoundTestRepository = weakSoundTestRepository;
        this.objectMapper = objectMapper;
        this.weakSoundTestAiService = weakSoundTestAiService;
        this.phonemeService = phonemeService;
        this.userWeakSoundRepository = userWeakSoundRepository;
        this.weakSoundTestSatusRepositoy = weakSoundTestSatusRepositoy;
    }

    public List<WeakSoundTestListDto> getWeakSoundTest() {
        List<WeakSoundTest> weakSoundTestList = weakSoundTestRepository.findAll();

        return weakSoundTestList.stream()
                .map(test -> new WeakSoundTestListDto(
                        test.getId(),
                        test.getText(),
                        test.getPronunciation(),
                        test.getEngPronunciation(),
                        test.getEngTranslation()))
                .collect(Collectors.toList());
    }

    public String processUserAudio(MultipartFile userAudio, WeakSoundTest weakSoundTest, Long userId) throws IOException {
        // userAudio<wav> -> userAudioBytes
        byte[] userAudioBytes = userAudio.getBytes();

        // userAudioBytes -> userAudioBase64
        String userAudioBase64 = Base64.getEncoder().encodeToString(userAudioBytes);

        Map<String, Object> dataToSend = new HashMap<>();
        dataToSend.put("userAudio", userAudioBase64);
        dataToSend.put("correctText", weakSoundTest.getText());

        TestResponseDto testResponse = weakSoundTestAiService.sendToAi(userId, dataToSend);
        return objectMapper.writeValueAsString(testResponse); // testResponse를 json 형식으로 변환
    }

    public Map<Long, Integer> getTopPhonemes(Long userId) {
        Map<Long, Integer> topPhonemes = phonemeService.getTopPhonemes(userId);

        if (topPhonemes == null || topPhonemes.isEmpty()) {
            throw new ResponseNotFoundException("취약음소가 없습니다."); // 404
        }

        topPhonemes.forEach((phonemeId, count) -> {
            UserWeakSound userWeakSound = new UserWeakSound(userId, phonemeId);
            userWeakSoundRepository.save(userWeakSound);
        });

        return topPhonemes;
    }

    public void finalizeTestStatus(Long userId) {
        try {
            WeakSoundTestStatus weakSoundTestStatus = new WeakSoundTestStatus(userId, true);
            weakSoundTestSatusRepositoy.save(weakSoundTestStatus);
        } finally {
            phonemeService.clearTemporaryData(userId);
        }
    }
}
