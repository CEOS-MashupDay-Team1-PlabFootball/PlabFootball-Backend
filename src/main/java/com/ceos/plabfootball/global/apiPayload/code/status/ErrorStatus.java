package com.ceos.plabfootball.global.apiPayload.code.status;

import com.ceos.plabfootball.global.apiPayload.code.BaseErrorCode;
import com.ceos.plabfootball.global.apiPayload.code.ErrorReasonDTO;
import lombok.Getter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
	_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
	_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
	_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청한 대상을 찾을 수 없습니다."),
	_METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "지원하지 않는 요청 방식입니다."),
	_UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "COMMON415", "지원하지 않는 요청 형식입니다."),
	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),

	MATCH_FILTER_REQUIRED(HttpStatus.BAD_REQUEST, "MATCH4001", "날짜 외 검색 필터를 하나 이상 선택해 주세요."),

	// 인증 관련 오류
	AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"AUTH_UNAUTHORIZED", "인증이 필요한 요청입니다."),
	AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"AUTH_INVALID_TOKEN", "유효하지 않은 Access Token입니다."),
	AUTH_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,	"AUTH_INVALID_REFRESH_TOKEN", "유효하지 않은 Refresh Token입니다."),
	AUTH_INVALID_GUEST(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_GUEST", "유효하지 않은 게스트 사용자입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ErrorReasonDTO getReason() {
		return ErrorReasonDTO.builder()
				.isSuccess(false)
				.code(code)
				.message(message)
				.build();
	}

	@Override
	public ErrorReasonDTO getReasonHttpStatus() {
		return ErrorReasonDTO.builder()
				.isSuccess(false)
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();
	}
}
