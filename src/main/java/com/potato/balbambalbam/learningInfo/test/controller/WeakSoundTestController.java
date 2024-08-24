package com.potato.balbambalbam.learningInfo.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.data.entity.UserWeakSound;
import com.potato.balbambalbam.data.entity.WeakSoundTest;
import com.potato.balbambalbam.data.entity.WeakSoundTestStatus;
import com.potato.balbambalbam.data.repository.UserWeakSoundRepository;
import com.potato.balbambalbam.data.repository.WeakSoundTestRepository;
import com.potato.balbambalbam.data.repository.WeakSoundTestSatusRepositoy;
import com.potato.balbambalbam.exception.ExceptionDto;
import com.potato.balbambalbam.exception.InvalidParameterException;
import com.potato.balbambalbam.exception.ParameterNotFoundException;
import com.potato.balbambalbam.exception.ResponseNotFoundException;
import com.potato.balbambalbam.profile.token.jwt.JWTUtil;
import com.potato.balbambalbam.profile.join.service.JoinService;
import com.potato.balbambalbam.learningInfo.test.dto.WeakSoundTestResponseDto;
import com.potato.balbambalbam.learningInfo.weaksound.service.PhonemeService;
import com.potato.balbambalbam.learningInfo.test.service.WeakSoundTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@Tag(name = "WeakSoundTest API", description = "사용자의 취약음소 테스트와 관련된 API를 제공한다.")
public class WeakSoundTestController {

    private final ObjectMapper objectMapper;
    private final WeakSoundTestService weakSoundTestService;
    private final WeakSoundTestRepository weakSoundTestRepository;
    private final PhonemeService phonemeService;
    private final UserWeakSoundRepository userWeakSoundRepository;
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy;

    public WeakSoundTestController(ObjectMapper objectMapper,
                                   WeakSoundTestService weakSoundTestService,
                                   WeakSoundTestRepository weakSoundTestRepository,
                                   PhonemeService phonemeService,
                                   UserWeakSoundRepository userWeakSoundRepository,
                                   JoinService joinService,
                                   JWTUtil jwtUtil,
                                   WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy){
        this.objectMapper = objectMapper;
        this.weakSoundTestService = weakSoundTestService;
        this.weakSoundTestRepository = weakSoundTestRepository;
        this.phonemeService = phonemeService;
        this.userWeakSoundRepository = userWeakSoundRepository;
        this.joinService = joinService;
        this.jwtUtil = jwtUtil;
        this.weakSoundTestSatusRepositoy = weakSoundTestSatusRepositoy;
    }

    private Long extractUserIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    @Operation(summary = "취약음소 테스트 목록 조회", description = "모든 취약음소 테스트 목록을 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 취약음소 테스트 목록을 반환한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WeakSoundTestResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 발생",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDto.class))
            )
    })
    @GetMapping("/test")
    public ResponseEntity<?> getWeakSoundTest() {
        List<WeakSoundTest> weakSoundTestList = weakSoundTestRepository.findAll();
        return ResponseEntity.ok(weakSoundTestList); //200
    }

    @Operation(summary = "사용자 음성 파일 업로드 및 테스트", description = "사용자 음성 파일을 업로드하고 AI와의 테스트 결과를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 테스트 결과를 반환한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "사용자 음성 파일이 비어있는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 ID를 가진 테스트 카드를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 발생",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDto.class))
            )
    })
    @PostMapping("/test/{cardId}")
    public ResponseEntity<String> uploadFile
            (@PathVariable("cardId") Long id,
             @RequestHeader("access") String access,
             @RequestParam("userAudio")MultipartFile userAudio) throws JsonProcessingException {

        Long userId = extractUserIdFromToken(access);

        if(userAudio.isEmpty()){
            throw new ParameterNotFoundException("사용자 음성 파일이 비었습니다."); //400
        }

        return weakSoundTestRepository.findById(id)
                .map(weakSoundTest -> {
                    try{
                        // userAudio<wav> -> userAudioBytes
                        byte[] userAudioBytes = userAudio.getBytes();
                        // userAudioBytes -> userAudioBase64
                        String userAudioBase64 = Base64.getEncoder().encodeToString(userAudioBytes);

                        Map<String, Object> dataToSend = new HashMap<>();
                        dataToSend.put("userAudio",userAudioBase64);
                        dataToSend.put("correctText",weakSoundTest.getText());

                        WeakSoundTestResponseDto testResponse = weakSoundTestService.sendToAi(userId, dataToSend);
                        String testResponseJson = objectMapper.writeValueAsString(testResponse);

                        return ResponseEntity.ok(testResponseJson);
                    } catch (IOException e){
                        throw new RuntimeException("서버 오류가 발생했습니다."); //500
                    }
                })
                .orElseThrow(() -> new InvalidParameterException("해당 id를 가진 테스트 카드가 없습니다.")); //404
    }

    @Operation(summary = "취약음소 분석 완료", description = "사용자의 취약음소 분석을 완료하고, 결과를 저장한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 취약음소 분석 결과를 저장한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "취약음소 분석 결과가 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 발생",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDto.class))
            )
    })
    @PostMapping("/test/finalize")
    public ResponseEntity<?> finalizeAnalysis(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        Map<Long, Integer> topPhonemes;

        try {
            topPhonemes = phonemeService.getTopPhonemes(userId);

            if (topPhonemes == null || topPhonemes.isEmpty()) {
                throw new ResponseNotFoundException("취약음소가 없습니다."); // 404
            }

            topPhonemes.forEach((phonemeId, count) -> {
                UserWeakSound userWeakSound = new UserWeakSound(userId, phonemeId);
                userWeakSoundRepository.save(userWeakSound);
            });

        } catch (ResponseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("서버 오류가 발생했습니다."); // 500
        } finally {
            WeakSoundTestStatus weakSoundTestStatus = new WeakSoundTestStatus(userId, true);
            weakSoundTestSatusRepositoy.save(weakSoundTestStatus);

            phonemeService.clearTemporaryData(userId);
        }

        return ResponseEntity.ok(topPhonemes); // 200
    }
}