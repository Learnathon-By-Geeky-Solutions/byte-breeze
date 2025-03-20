package com.bytebreeze.quickdrop.dto;

import com.bytebreeze.quickdrop.enums.VerificationStatus;
import lombok.Data;

@Data
public class RiderDashboardResponseDTO {

	private String fullName;

	private VerificationStatus verificationStatus;

	private Boolean isAvailable;

	private double riderAvgRating;

	private double riderBalance;
}
