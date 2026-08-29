package com.sinchonthon.team3_backend.domain.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "서버 상태 확인 (인증 불필요)")
@RestController
public class HealthController {

    @Operation(summary = "헬스체크", security = {})
    @GetMapping("/health")
    public String healthCheck() {
        return "sinchonping is alive!";
    }
}
