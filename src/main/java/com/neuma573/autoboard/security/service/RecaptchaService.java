package com.neuma573.autoboard.security.service;

import com.google.recaptchaenterprise.v1.*;
import com.neuma573.autoboard.global.exception.RecaptchaValidationException;
import com.neuma573.autoboard.global.model.dto.RecaptchaResponse;
import com.neuma573.autoboard.global.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient;
import com.google.recaptchaenterprise.v1.RiskAnalysis.ClassificationReason;
import java.io.IOException;

@Slf4j
@Service
public class RecaptchaService {

    @Value("${app.recaptcha.v2.site-key}")
    private String recaptchaV2SiteKey;

    @Value("${app.recaptcha.v3.site-key}")
    private String recaptchaV3SiteKey;

    @Value("${app.recaptcha.project-id}")
    private String recaptchaProjectId;


    public RecaptchaResponse createAssessment(HttpServletRequest httpServletRequest)
            throws IOException {
        // reCAPTCHA 클라이언트를 만듭니다.
        // 할 일: 클라이언트 생성 코드를 캐시하거나(권장) 메서드를 종료하기 전에 client.close()를 호출합니다.
        String recaptchaToken = RequestUtils.getHeader(httpServletRequest, "Recaptcha-Token");
        String recaptchaAction = RequestUtils.getHeader(httpServletRequest, "Action-Name");
        String recaptchaVersion = RequestUtils.getHeader(httpServletRequest, "Recaptcha-Version");
        if (recaptchaVersion == null || recaptchaVersion.isEmpty() || recaptchaToken == null || recaptchaToken.isEmpty() || recaptchaAction == null || recaptchaAction.isEmpty()) {
            throw new RecaptchaValidationException("Recaptcha Token is invalid");
        }

        try (RecaptchaEnterpriseServiceClient client = RecaptchaEnterpriseServiceClient.create()) {

            // 추적할 이벤트의 속성을 설정합니다.
            Event event = Event.newBuilder()
                    .setSiteKey(recaptchaVersion.equals("v3") ? recaptchaV3SiteKey : recaptchaV2SiteKey)
                    .setToken(recaptchaToken)
                    .setUserAgent(RequestUtils.getUserAgent(httpServletRequest))
                    .setRequestedUri(RequestUtils.getRequestUri(httpServletRequest))
                    .setUserIpAddress(RequestUtils.getClientIpAddress(httpServletRequest))
                    .build();

            // 평가 요청을 작성합니다.
            CreateAssessmentRequest createAssessmentRequest =
                    CreateAssessmentRequest.newBuilder()
                            .setParent(ProjectName.of(recaptchaProjectId).toString())
                            .setAssessment(Assessment.newBuilder().setEvent(event).build())
                            .build();

            Assessment response = client.createAssessment(createAssessmentRequest);

            // 토큰의 유효성과 예상한 작업의 실행을 확인합니다.
            if (!response.getTokenProperties().getValid()) {
                throw new RecaptchaValidationException("The CreateAssessment call failed because the token was: " + response.getTokenProperties().getInvalidReason().name());
            } else if(recaptchaVersion.equals("v2")) {
                return RecaptchaResponse
                        .builder()
                        .score(0.9f)
                        .success(true)
                        .build();
            }



            if (!response.getTokenProperties().getAction().equals(recaptchaAction)) {
                throw new RecaptchaValidationException("The action attribute in the reCAPTCHA tag does not match the action (" + recaptchaAction + ") you are expecting to score");
            }
            // 위험 점수와 이유를 가져옵니다.
            // 평가 해석에 대한 자세한 내용은 다음을 참조하세요.
            // https://cloud.google.com/recaptcha-enterprise/docs/interpret-assessment
            for (ClassificationReason reason : response.getRiskAnalysis().getReasonsList()) {
                log.info(String.valueOf(reason));
            }

            float recaptchaScore = response.getRiskAnalysis().getScore();
            log.info("The reCAPTCHA score is: " + recaptchaScore);
            if (recaptchaScore < 0.5) { // 임계값 설정
                throw new RecaptchaValidationException("reCAPTCHA score is too low.");
            }
            // 평가 이름(ID)을 가져옵니다. 평가에 주석을 추가하는 데 사용합니다.
            String assessmentName = response.getName();
            log.info("Assessment name: " + assessmentName.substring(assessmentName.lastIndexOf("/") + 1));

            return RecaptchaResponse
                    .builder()
                    .score(recaptchaScore)
                    .success(true)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return RecaptchaResponse
                    .builder()
                    .score(0f)
                    .success(false)
                    .build();
        }
    }
}
