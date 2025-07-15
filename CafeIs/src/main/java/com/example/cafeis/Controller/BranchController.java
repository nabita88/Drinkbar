package com.example.cafeis.Controller;

import com.example.cafeis.DTO.ApiResponseDTO;
import com.example.cafeis.DTO.BranchDTO;
import com.example.cafeis.DTO.ErrorResponseDTO;
import com.example.cafeis.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/Branches")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Branch", description = "점포 정보 서비스 API")
public class BranchController {

    private final BranchService branchService;

    @Operation(summary = "점포 상세 확인 API", description = "선택한 점포의 세부 데이터를 가져옵니다", tags = {"Store"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))})
    @GetMapping("/{branchNo}")
    public ResponseEntity<ApiResponseDTO<BranchDTO>> fetchBranchDetails(@PathVariable Long branchNo) {
        return Optional.of(branchNo)
                .map(id -> {
                    log.debug("점포 상세 정보 조회 시작 - 점포 번호: {}", id);
                    return branchService.fetchBranchDetails(id);
                })
                .map(branch -> {
                    log.info("점포 상세 정보 조회 성공 - 점포 번호: {}, 점포명: {}",
                            branchNo, branch.getBranchTitle());
                    return ApiResponseDTO.success(branch);
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.error("점포 상세 정보 조회 실패 - 존재하지 않는 점포 번호: {}", branchNo);
                    return ResponseEntity.badRequest()
                            .body(ApiResponseDTO.error("점포 정보를 찾을 수 없습니다"));
                });
    }
}
