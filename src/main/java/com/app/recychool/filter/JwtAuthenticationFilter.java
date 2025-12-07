package com.app.recychool.filter;

import com.app.recychool.domain.dto.UserResponseDTO;
import com.app.recychool.service.UserService;
import com.app.recychool.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// private이 붙어있는 경로는 모두 header에서 토큰을 검증한다.
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // WebSocket 경로는 필터 건너뛰기 (WebSocket 테스트용 - 주석 처리 가능)
        // if (path.startsWith("/ws")) {
        //   return true;
        // }
        // private 경로가 아닌 경우 필터 건너뛰기
        return !path.startsWith("/private/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        log.info("🔐 JWT 필터 진입: {}", path);
        
        String header = request.getHeader("Authorization");
        String jwtToken = null;
        String userEmail = null;

        // 헤더에 심어져있는 userEmail을 가져온다
        if(header != null && header.startsWith("Bearer ")){
            jwtToken = header.substring(7);
            log.info("🔐 JWT 토큰 발견: {}", jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");
            
            boolean isValid = jwtTokenUtil.verifyJwtToken(jwtToken);
            log.info("🔐 JWT 토큰 검증 결과: {}", isValid);
            
            if(isValid){
                try {
                    userEmail = (String)jwtTokenUtil.getUserEmailFromToken(jwtToken).get("userEmail");
                    log.info("🔐 토큰에서 추출한 userEmail: {}", userEmail);
                } catch (Exception e) {
                    log.error("🔐 토큰에서 userEmail 추출 실패", e);
                }
            }
        } else {
            log.warn("🔐 Authorization 헤더가 없거나 Bearer로 시작하지 않음: {}", header);
        }

        // 인증된 사용자 정보 객체를 생성하고 전달
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            try {
                Long userId = userService.getUserIdByUserEmail(userEmail);
                log.info("🔐 userEmail로 찾은 userId: {}", userId);
                
                UserResponseDTO foundUser = userService.getUserById(userId);
                log.info("🔐 찾은 사용자: {}", foundUser.getUserEmail());

                if(jwtTokenUtil.verifyJwtToken(jwtToken)){
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(foundUser, null, List.of());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("🔐 SecurityContext에 Authentication 설정 완료");
                }
            } catch (Exception e) {
                log.error("🔐 사용자 조회 또는 인증 설정 실패", e);
            }
        } else {
            log.warn("🔐 인증 설정 실패 - userEmail: {}, 기존 인증: {}", userEmail, SecurityContextHolder.getContext().getAuthentication() != null);
        }
        
        filterChain.doFilter(request, response);
    }
}
