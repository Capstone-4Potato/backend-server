package com.potato.balbambalbam.learningInfo.test.controller;

import com.potato.balbambalbam.data.entity.WeakSoundTest;
import com.potato.balbambalbam.data.repository.WeakSoundTestRepository;
import com.potato.balbambalbam.log.dto.ExceptionDto;
import com.potato.balbambalbam.exception.InvalidParameterException;
import com.potato.balbambalbam.exception.ParameterNotFoundException;
import com.potato.balbambalbam.learningInfo.test.dto.WeakSoundTestListDto;
import com.potato.balbambalbam.learningInfo.test.service.WeakSoundTestService;
import com.potato.balbambalbam.profile.token.jwt.JWTUtil;
import com.potato.balbambalbam.profile.join.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@Tag(name = "WeakSoundTest API", description = "사용자의 취약음소 테스트와 관련된 API를 제공한다.")
public class WeakSoundTestController {

    private final WeakSoundTestRepository weakSoundTestRepository;
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final WeakSoundTestService weakSoundTestService;

    public WeakSoundTestController(WeakSoundTestRepository weakSoundTestRepository,
                                   JoinService joinService,
                                   JWTUtil jwtUtil,
                                   WeakSoundTestService weakSoundTestService){
        this.weakSoundTestRepository = weakSoundTestRepository;
        this.joinService = joinService;
        this.jwtUtil = jwtUtil;
        this.weakSoundTestService = weakSoundTestService;
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
                            schema = @Schema(implementation = WeakSoundTestListDto.class))
            )
    })
    @GetMapping("/test")
    public ResponseEntity<List<WeakSoundTestListDto>> getWeakSoundTest() {
        List<WeakSoundTestListDto> responseDtoList = weakSoundTestService.getWeakSoundTest();
        return ResponseEntity.ok(responseDtoList);
    }

    @Operation(summary = "사용자 음성 파일 업로드 및 테스트", description = "사용자 음성 파일을 업로드하고 AI와의 테스트 결과를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 테스트 결과를 반환한 경우",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"userWeakPhoneme\": {\"ㄲ\" : 5, \"ㅏ\" : 2, \" \" : 1}, \"userWeakPhonemeLast\": {\"ㄱ\" : 2}}"))
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
            )
    })
    @PostMapping("/test/{cardId}")
    public ResponseEntity<String> uploadFile(
            @PathVariable("cardId") Long id,
            @RequestHeader("access") String access,
            @RequestParam("userAudio") MultipartFile userAudio) throws IOException {

        Long userId = extractUserIdFromToken(access);

        if (userAudio.isEmpty()) {
            throw new ParameterNotFoundException("사용자 음성 파일이 비었습니다."); //400
        }

        WeakSoundTest weakSoundTest = weakSoundTestRepository.findById(id)
                .orElseThrow(() -> new InvalidParameterException("해당 id를 가진 테스트 카드가 없습니다.")); //404

        String testResponseJson = weakSoundTestService.processUserAudio(userAudio, weakSoundTest, userId);

        return ResponseEntity.ok(testResponseJson);
    }

    @Operation(summary = "취약음소 분석 완료", description = "사용자의 취약음소 분석을 완료하고, 결과를 저장한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "취약음소가 있는 경우",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"1\": 3, \"14\": 2, \"40\": 1, \"phonemeId\": \"count\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "취약음소가 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDto.class))
            )
    })
    @PostMapping("/test/finalize")
    public ResponseEntity<Map<Long, Integer>> finalizeAnalysis(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        Map<Long, Integer> topPhonemes = weakSoundTestService.getTopPhonemes(userId);

        weakSoundTestService.finalizeTestStatus(userId);

        return ResponseEntity.ok(topPhonemes);
    }
}