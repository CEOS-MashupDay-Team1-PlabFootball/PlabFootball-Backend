package com.ceos.plabfootball.global.apiPayload.exception;

import com.ceos.plabfootball.global.apiPayload.code.BaseErrorCode;
import com.ceos.plabfootball.global.apiPayload.code.ErrorReasonDTO;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

	private final BaseErrorCode code;

	public GeneralException(BaseErrorCode code) {
		super(code.getReason().getMessage());
		this.code = code;
	}

	public ErrorReasonDTO getErrorReason() {
		return code.getReason();
	}

	public ErrorReasonDTO getErrorReasonHttpStatus() {
		return code.getReasonHttpStatus();
	}
}
