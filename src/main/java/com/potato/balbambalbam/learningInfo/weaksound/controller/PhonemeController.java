package com.potato.balbambalbam.learningInfo.weaksound.controller;

import com.potato.balbambalbam.data.entity.Phoneme;
import com.potato.balbambalbam.data.entity.UserWeakSound;
import com.potato.balbambalbam.data.entity.WeakSoundTestStatus;
import com.potato.balbambalbam.data.repository.PhonemeRepository;
import com.potato.balbambalbam.data.repository.UserWeakSoundRepository;
import com.potato.balbambalbam.data.repository.WeakSoundTestSatusRepositoy;
import com.potato.balbambalbam.exception.ExceptionDto;
import com.potato.balbambalbam.exception.ResponseNotFoundException;
import com.potato.balbambalbam.learningInfo.weaksound.dto.UserWeakSoundResponseDto;
import com.potato.balbambalbam.profile.token.jwt.JWTUtil;
import com.potato.balbambalbam.profile.join.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@Slf4j
@Tag(name = "WeakSound API", description = "사용자의 취약음소와 관련된 API를 제공한다.")
public class PhonemeController {

    private final UserWeakSoundRepository userWeakSoundRepository;
    private final PhonemeRepository phonemeRepository;
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy;

    public PhonemeController (UserWeakSoundRepository userWeakSoundRepository,
                              PhonemeRepository phonemeRepository,
                              JoinService joinService,
                              JWTUtil jwtUtil,
                              WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy){
        this.userWeakSoundRepository = userWeakSoundRepository;
        this.phonemeRepository = phonemeRepository;
        this.joinService = joinService;
        this.jwtUtil = jwtUtil;
        this.weakSoundTestSatusRepositoy = weakSoundTestSatusRepositoy;
    }

    private Long extractUserIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    @Operation(summary = "취약음소 테스트 상태 확인", description = "사용자가 취약음소 테스트를 완료했는지 여부를 제공한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "테스트를 한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "테스트를 하지 않은 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDto.class))
            )
    })
    @GetMapping("/test/status")
    public ResponseEntity<Boolean> getTestStatusByUserId(@RequestHeader("access") String access){
        Long userId = extractUserIdFromToken(access);

        WeakSoundTestStatus weakSoundTestStatus = weakSoundTestSatusRepositoy.findByUserId(userId);

        if (weakSoundTestStatus == null) {
            throw new ResponseNotFoundException("취약음소 테스트가 필요합니다."); // 404
        }

        return ResponseEntity.ok(weakSoundTestStatus.getTestStatus()); // 200
    }

    @Operation(summary = "사용자의 취약음소 제공", description = "사용자의 취약음소 4개를 제공한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자의 취약음소가 있는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserWeakSoundResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자의 취약음소가 없는 경우",
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
    @GetMapping("/test/phonemes")
    public ResponseEntity<?> getWeakPhonemesByUserId(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        try {
            List<UserWeakSound> weakPhonemes = userWeakSoundRepository.findAllByUserId(userId);

            if (weakPhonemes == null || weakPhonemes.isEmpty()) {
                throw new ResponseNotFoundException("취약음소가 없습니다."); // 404
            }

            List<UserWeakSoundResponseDto> weakPhonemeDtos = IntStream.range(0, weakPhonemes.size())
                    .mapToObj(index -> {
                        UserWeakSound weakPhoneme = weakPhonemes.get(index);
                        Phoneme phoneme = phonemeRepository.findById(weakPhoneme.getUserPhoneme()).orElse(null);
                        String phonemeType = null;
                        String phonemeText = null;
                        if (phoneme != null) {
                            switch (phoneme.getType().intValue()) {
                                case 0:
                                    phonemeType = "Initial consonant";
                                    break;
                                case 1:
                                    phonemeType = "Medial vowel";
                                    break;
                                case 2:
                                    phonemeType = "Final consonant";
                                    break;
                                default:
                                    phonemeType = "?";
                            }
                            phonemeText = phonemeType + " " + phoneme.getText();
                        }
                        return new UserWeakSoundResponseDto(index + 1, phonemeText); // 순위는 1부터 시작
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(weakPhonemeDtos);
        } catch (ResponseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("서버 오류가 발생했습니다."); // 500
        }
    }

    @Operation(summary = "사용자의 취약음소 및 테스트 상태 삭제", description = "사용자의 모든 취약음소 데이터와 테스트 상태를 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자의 취약음소 데이터와 테스트 상태가 성공적으로 삭제된 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제할 취약음소 데이터가 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류가 발생한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDto.class))
            )
    })
    @DeleteMapping("/phonemes")
    public ResponseEntity<?> deleteWeakPhonemesByUserId(@RequestHeader("access") String access) {
        Long userId = extractUserIdFromToken(access);
        try {
            List<UserWeakSound> userWeakSounds = userWeakSoundRepository.findAllByUserId(userId);

            if (userWeakSounds != null && !userWeakSounds.isEmpty()) {
                userWeakSoundRepository.deleteAll(userWeakSounds);
            } else {
                throw new ResponseNotFoundException("취약음소가 없습니다."); // 404
            }

        } catch (ResponseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("서버 오류가 발생했습니다."); // 500
        } finally {
            WeakSoundTestStatus weakSoundTestStatus = weakSoundTestSatusRepositoy.findByUserId(userId);
            weakSoundTestSatusRepositoy.delete(weakSoundTestStatus);
        }
        return ResponseEntity.ok("사용자의 취약음소 데이터가 삭제되었습니다."); //200
    }
}