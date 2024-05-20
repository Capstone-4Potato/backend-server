package com.potato.balbambalbam.main.cardList.service;

import com.potato.balbambalbam.data.entity.PronunciationPicture;
import com.potato.balbambalbam.data.repository.PronunciationPictureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateImageService {
    private final PronunciationPictureRepository pronunciationRepository;
    @Transactional
    public void saveImage() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:static/images/*.png");
        Arrays.stream(resources).forEach(resource -> {
            try{
                String filename = resource.getFilename();
                String numberOnly = filename.replaceAll("[^0-9]", "");
                PronunciationPicture pronunciationPicture = pronunciationRepository.findByPhonemeId(Long.parseLong(numberOnly)).orElseThrow(() -> new IllegalArgumentException("잘못된 이미지 이름"));
                if(pronunciationPicture.getPicture().length()==0){
                    byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
                    pronunciationPicture.setPicture(Base64.getEncoder().encodeToString(bytes));
                    pronunciationRepository.save(pronunciationPicture);

                    log.info("[INSERT IMAGE] : {}", pronunciationPicture.getPhonemeId());
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }

}
