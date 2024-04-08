package com.potato.balbambalbam.main.service;

import com.potato.balbambalbam.entity.Phoneme;
import com.potato.balbambalbam.main.repository.PhonemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 음소 단위로 분류하고 각 음소와 일치하는 음소테이블의 아이디와 연결
 */
@Service
@RequiredArgsConstructor
public class PhonemeService {
    private final PhonemeRepository phonemeRepository;

    private static final String[] CHOSUNG = {
            "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
    };
    private static final String[] JUNGSUNG = {
            "ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅘ", "ㅙ", "ㅚ", "ㅛ", "ㅜ", "ㅝ", "ㅞ", "ㅟ", "ㅠ", "ㅡ", "ㅢ", "ㅣ"
    };
    //첫 번째 값은 종성이 없는 경우를 위해 빈 문자열("")를 사용
    private static final String[] JONGSUNG = {
            "", "ㄱ", "ㄲ", "ㄳ", "ㄴ", "ㄵ", "ㄶ", "ㄷ", "ㄹ", "ㄺ", "ㄻ", "ㄼ", "ㄽ", "ㄾ", "ㄿ", "ㅀ", "ㅁ", "ㅂ", "ㅄ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
    };

    public List<Long> convertTextToPhonemeIds(String text){
        List<String[]> phonemes = extractPhonemesFromText(text);
        List<Long> phonemeIds = findPhonemeIds(phonemes);

        return phonemeIds;
    }
    protected List<String[]> extractPhonemesFromText(String text){
        List<String[]> phonemeList = new ArrayList<>();

        char[] charText = text.toCharArray();
        //다 분류

        for (char ch : charText) {
            if(!(ch >= '가' && ch <= '힣')){
                continue;
            }
            ch = (char)(ch - 0xAC00);
            char cho = (char)(ch/28/21);
            char jung = (char)(ch/28%21);
            char jong = (char)(ch%28);
            String[] phoneme = {CHOSUNG[cho], JUNGSUNG[jung], JONGSUNG[jong]};
            phonemeList.add(phoneme);
        }

        return phonemeList;
    }

    protected List<Long> findPhonemeIds(List<String[]> phonemes){
        List<Long> phonemeIds = new ArrayList<>();

        //한 단어씩 초성 중성 종성 아이디 찾음
        phonemes.stream().forEach(strings -> {
            for (int i = 0; i < 3; i++) {   //0 : 초성 , 1 : 중성 , 2 : 초성
                Optional<Phoneme> phonemeOptional = phonemeRepository.findPhonemeByTypeAndText(Long.valueOf(i), strings[i]);
                if(phonemeOptional.isPresent()){    //받침이 없는 경우 넘어감
                    Phoneme phoneme = phonemeOptional.get();
                    phonemeIds.add(phoneme.getId());
                }
            }}
        );

        List<Long> distinctPhonemeIds= phonemeIds.stream().distinct().collect(Collectors.toList());    //중복 제거

        return distinctPhonemeIds;
    }
}