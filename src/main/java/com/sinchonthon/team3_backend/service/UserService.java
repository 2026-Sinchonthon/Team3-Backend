package com.sinchonthon.team3_backend.service;

import com.sinchonthon.team3_backend.domain.user.User;
import com.sinchonthon.team3_backend.dto.request.OnboardingRequest;
import com.sinchonthon.team3_backend.dto.response.OnboardingResponse;
import com.sinchonthon.team3_backend.exception.ApiException;
import com.sinchonthon.team3_backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    @Transactional
    public OnboardingResponse updateOnboarding(Long userId, OnboardingRequest request) {
        User user = users.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        String nickname = request.nickname().trim();
        if (nickname.length() < 2 || nickname.length() > 30) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "닉네임은 2자 이상 30자 이하여야 합니다.");
        }
        if (users.existsByNicknameAndIdNot(nickname, userId)) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }
        user.updateOnboarding(nickname, request.livingAloneYears());
        return new OnboardingResponse(user.getId(), user.getNickname(), user.getLivingAloneYears());
    }

    @Transactional
    public void deleteAccount(Long userId) {
        User user = users.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        users.deleteCommentsByUserId(userId);
        users.deleteTipsByUserId(userId);
        users.delete(user);
    }
}
