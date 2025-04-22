package com.bytebreeze.quickdrop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.bytebreeze.quickdrop.dto.response.RiderDashboardResponseDTO;
import com.bytebreeze.quickdrop.dto.request.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.dto.request.RiderRegistrationRequestDTO;
import com.bytebreeze.quickdrop.dto.response.RiderViewCurrentParcelsResponseDTO;
import com.bytebreeze.quickdrop.enums.ParcelStatus;
import com.bytebreeze.quickdrop.enums.VerificationStatus;
import com.bytebreeze.quickdrop.exception.custom.AlreadyExistsException;
import com.bytebreeze.quickdrop.exception.custom.ParcelNotFoundException;
import com.bytebreeze.quickdrop.entity.Parcel;
import com.bytebreeze.quickdrop.entity.Rider;
import com.bytebreeze.quickdrop.service.ParcelService;
import com.bytebreeze.quickdrop.service.RiderService;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ExtendWith(MockitoExtension.class)
class RiderControllerTest {

	@Mock
	private RiderService riderService;

	@Mock
	private ParcelService parcelService;

	@Mock
	private Model model;

	@Mock
	private BindingResult bindingResult;

	@Mock
	private RedirectAttributes redirectAttributes;

	@InjectMocks
	private RiderController riderController;

	private UUID riderId;
	private UUID parcelId;
	private Rider rider;
	private RiderRegistrationRequestDTO registrationDTO;
	private RiderOnboardingDTO onboardingDTO;
	private RiderDashboardResponseDTO dashboardResponse;
	private RiderViewCurrentParcelsResponseDTO parcelDTO;
	private Parcel parcel;

	@BeforeEach
	void setUp() {
		riderId = UUID.randomUUID();
		parcelId = UUID.randomUUID();

		rider = new Rider();
		rider.setId(riderId);
		rider.setFullName("John Doe");
		rider.setEmail("john@example.com");
		rider.setIsAssigned(false);

		registrationDTO = new RiderRegistrationRequestDTO();
		registrationDTO.setFullName("John Doe");
		registrationDTO.setPhoneNumber("1234567890");
		registrationDTO.setEmail("john@example.com");
		registrationDTO.setPassword("Password@123");

		onboardingDTO = new RiderOnboardingDTO();

		dashboardResponse = new RiderDashboardResponseDTO();
		dashboardResponse.setFullName("John Doe");
		dashboardResponse.setVerificationStatus(VerificationStatus.PENDING);
		dashboardResponse.setIsAvailable(true);
		dashboardResponse.setRiderAvgRating(4.5);
		dashboardResponse.setRiderBalance(100.0);

		parcelDTO = new RiderViewCurrentParcelsResponseDTO();
		parcelDTO.setId(parcelId);
		parcelDTO.setTrackingId("123456");
		parcelDTO.setPrice(new BigDecimal("100.00"));

		parcel = new Parcel();
		parcel.setId(parcelId);
		parcel.setTrackingId("123456");
		parcel.setStatus(ParcelStatus.ASSIGNED);
		parcel.setPrice(new BigDecimal("100.00"));
	}

	@Test
	void testRiderLogin() {
		String view = riderController.riderLogin(model);
		assertEquals("auth/rider-login", view);
		verifyNoInteractions(model);
	}

	@Test
	void testRiderDashboard() {
		when(riderService.riderDashboardResponse()).thenReturn(dashboardResponse);

		String view = riderController.riderDashboard(model);

		assertEquals("dashboard/rider-dashboard", view);
		verify(model).addAttribute("riderDashboardResponseDTO", dashboardResponse);
		verify(riderService).riderDashboardResponse();
	}

	@Test
	void testShowRegistrationForm() {
		String view = riderController.showRegistrationForm(model);

		assertEquals("auth/rider-register", view);
		verify(model).addAttribute(eq("riderRegistrationRequestDTO"), any(RiderRegistrationRequestDTO.class));
	}

	@Test
	void testSubmitRegistrationForm_Success() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(riderService.registerRider(any(RiderRegistrationRequestDTO.class))).thenReturn(rider);

		String view = riderController.submitRegistrationForm(registrationDTO, bindingResult, model, redirectAttributes);

		assertEquals("redirect:/rider/onboarding/" + riderId, view);
		verify(riderService).registerRider(registrationDTO);
		verify(redirectAttributes).addFlashAttribute("success", "Rider Registration Successful");
		verifyNoInteractions(model);
	}

	@Test
	void testSubmitRegistrationForm_ValidationErrors() {
		when(bindingResult.hasErrors()).thenReturn(true);
		when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

		String view = riderController.submitRegistrationForm(registrationDTO, bindingResult, model, redirectAttributes);

		assertEquals("auth/rider-register", view);
		verify(model).addAttribute(eq("validationErrors"), anyList());
		verify(model).addAttribute("error");
		verifyNoInteractions(riderService, redirectAttributes);
	}

	@Test
	void testSubmitRegistrationForm_Exception() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(riderService.registerRider(any(RiderRegistrationRequestDTO.class)))
				.thenThrow(new AlreadyExistsException("Email already exists"));

		String view = riderController.submitRegistrationForm(registrationDTO, bindingResult, model, redirectAttributes);

		assertEquals("auth/rider-register", view);
		verify(model).addAttribute("errorMessage", "Email already exists");
		verify(riderService).registerRider(registrationDTO);
		verifyNoInteractions(redirectAttributes);
	}

	@Test
	void testShowRiderOnboardingForm_Success() {
		when(riderService.findByRiderId(riderId)).thenReturn(rider);
		when(model.containsAttribute("riderOnboardingDTO")).thenReturn(false);

		String view = riderController.showRiderOnboardingForm(riderId, model);

		assertEquals("rider/onboarding", view);
		verify(model).addAttribute("rider", rider);
		verify(model).addAttribute(eq("riderOnboardingDTO"), any(RiderOnboardingDTO.class));
		verify(riderService).findByRiderId(riderId);
	}

	@Test
	void testShowRiderOnboardingForm_WithExistingDTO() {
		when(riderService.findByRiderId(riderId)).thenReturn(rider);
		when(model.containsAttribute("riderOnboardingDTO")).thenReturn(true);

		String view = riderController.showRiderOnboardingForm(riderId, model);

		assertEquals("rider/onboarding", view);
		verify(model).addAttribute("rider", rider);
		verify(model, never()).addAttribute(eq("riderOnboardingDTO"), any(RiderOnboardingDTO.class));
		verify(riderService).findByRiderId(riderId);
	}

	@Test
	void testShowRiderOnboardingForm_Exception() {
		when(riderService.findByRiderId(riderId)).thenThrow(new RuntimeException("Rider not found"));

		String view = riderController.showRiderOnboardingForm(riderId, model);

		assertEquals("auth/rider-register", view);
		verify(model).addAttribute("errorMessage", "Rider not found");
		verify(riderService).findByRiderId(riderId);
	}

	@Test
	void testSubmitRiderOnboardingForm_Success() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(riderService.onboardRider(riderId, onboardingDTO)).thenReturn(rider);

		String view = riderController.submitRiderOnboardingForm(
				riderId, onboardingDTO, bindingResult, model, redirectAttributes);

		assertEquals("redirect:/rider/login", view);
		verify(riderService).onboardRider(riderId, onboardingDTO);
		verify(redirectAttributes).addFlashAttribute("success", "Rider Onboarded successfully. Verification Pending.");
		verifyNoInteractions(model);
	}

	@Test
	void testSubmitRiderOnboardingForm_ValidationErrors() {
		when(bindingResult.hasErrors()).thenReturn(true);
		when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

		String view = riderController.submitRiderOnboardingForm(
				riderId, onboardingDTO, bindingResult, model, redirectAttributes);

		assertEquals("redirect:/rider/onboarding/" + riderId, view);
		verify(redirectAttributes).addFlashAttribute(eq("validationErrors"), anyList());
		verify(redirectAttributes).addFlashAttribute("riderOnboardingDTO", onboardingDTO);
		verify(model).addAttribute("error");
		verifyNoInteractions(riderService);
	}

	@Test
	void testSubmitRiderOnboardingForm_Exception() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(riderService.onboardRider(riderId, onboardingDTO)).thenThrow(new RuntimeException("Onboarding failed"));

		String view = riderController.submitRiderOnboardingForm(
				riderId, onboardingDTO, bindingResult, model, redirectAttributes);

		assertEquals("redirect:/rider/onboarding/" + riderId, view);
		verify(redirectAttributes).addFlashAttribute("errorMessage", "Onboarding failed: Onboarding failed");
		verify(redirectAttributes).addFlashAttribute("riderOnboardingDTO", onboardingDTO);
		verify(riderService).onboardRider(riderId, onboardingDTO);
		verifyNoInteractions(model);
	}

	@Test
	void testUpdateStatus() {
		doNothing().when(riderService).updateRiderStatus(true);

		String view = riderController.updateStatus(true);

		assertEquals("redirect:/rider/dashboard", view);
		verify(riderService).updateRiderStatus(true);
	}

	@Test
	void testShowCurrentParcelsRequest_RiderAssigned() {
		rider.setIsAssigned(true);
		when(riderService.getAuthenticatedRider()).thenReturn(rider);
		when(riderService.getAssignedParcelByRider(rider)).thenReturn(Collections.singletonList(parcel));

		String view = riderController.showCurrentParcelsRequest(model);

		assertEquals("rider/view-assigned-parcels", view);
		verify(model).addAttribute("parcels", Collections.singletonList(parcel));
		verify(riderService).getAuthenticatedRider();
		verify(riderService).getAssignedParcelByRider(rider);
		verifyNoInteractions(parcelService);
	}

	@Test
	void testShowCurrentParcelsRequest_RiderAssigned_NoParcels() {
		rider.setIsAssigned(true);
		when(riderService.getAuthenticatedRider()).thenReturn(rider);
		when(riderService.getAssignedParcelByRider(rider)).thenReturn(null);

		String view = riderController.showCurrentParcelsRequest(model);

		assertEquals("rider/view-assigned-parcels", view);
		verify(model).addAttribute("error", "No parcel assigned despite rider being marked as assigned");
		verify(riderService).getAuthenticatedRider();
		verify(riderService).getAssignedParcelByRider(rider);
		verifyNoInteractions(parcelService);
	}

	@Test
	void testShowCurrentParcelsRequest_RiderAssigned_Exception() {
		rider.setIsAssigned(true);
		when(riderService.getAuthenticatedRider()).thenReturn(rider);
		when(riderService.getAssignedParcelByRider(rider)).thenThrow(new RuntimeException("Database error"));

		String view = riderController.showCurrentParcelsRequest(model);

		assertEquals("rider/view-assigned-parcels", view);
		verify(model).addAttribute("error", "Failed to load parcel : Database error");
		verify(riderService).getAuthenticatedRider();
		verify(riderService).getAssignedParcelByRider(rider);
		verifyNoInteractions(parcelService);
	}

	@Test
	void testShowCurrentParcelsRequest_RiderNotAssigned() {
		when(riderService.getAuthenticatedRider()).thenReturn(rider);
		when(riderService.currentParcelsForRider()).thenReturn(Collections.singletonList(parcelDTO));

		String view = riderController.showCurrentParcelsRequest(model);

		assertEquals("rider/view-current-parcels", view);
		verify(model).addAttribute("parcels", Collections.singletonList(parcelDTO));
		verify(riderService).getAuthenticatedRider();
		verify(riderService).currentParcelsForRider();
		verifyNoInteractions(parcelService);
	}

	@Test
	void testAcceptParcel_Success() {
		doNothing().when(riderService).acceptParcelDelivery(parcelId);

		String view = riderController.acceptParcel(parcelId, redirectAttributes);

		assertEquals("redirect:/rider/current-parcels", view);
		verify(riderService).acceptParcelDelivery(parcelId);
		verify(redirectAttributes).addFlashAttribute("success", "Parcel accepted successfully!");
	}

	@Test
	void testAcceptParcel_Exception() {
		doThrow(new ParcelNotFoundException("Parcel not found"))
				.when(riderService)
				.acceptParcelDelivery(parcelId);

		String view = riderController.acceptParcel(parcelId, redirectAttributes);

		assertEquals("redirect:/rider/current-parcels", view);
		verify(riderService).acceptParcelDelivery(parcelId);
		verify(redirectAttributes).addFlashAttribute("error", "Failed to accept parcel: Parcel not found");
	}

	@Test
	void testUpdateAssignedParcelStatus_Success() {
		doNothing().when(parcelService).updateParcelStatus(parcelId, ParcelStatus.PICKED_UP);

		String view =
				riderController.updateAssignedParcelStatus(parcelId, ParcelStatus.PICKED_UP, redirectAttributes, model);

		assertEquals("redirect:/rider/current-parcels", view);
		verify(parcelService).updateParcelStatus(parcelId, ParcelStatus.PICKED_UP);
		verify(redirectAttributes).addFlashAttribute("success", "Parcel status updated successfully!");
		verifyNoInteractions(model);
	}

	@Test
	void testUpdateAssignedParcelStatus_Exception() {
		doThrow(new RuntimeException("Invalid status"))
				.when(parcelService)
				.updateParcelStatus(parcelId, ParcelStatus.PICKED_UP);

		String view =
				riderController.updateAssignedParcelStatus(parcelId, ParcelStatus.PICKED_UP, redirectAttributes, model);

		assertEquals("redirect:/rider/current-parcels", view);
		verify(parcelService).updateParcelStatus(parcelId, ParcelStatus.PICKED_UP);
		verify(redirectAttributes).addFlashAttribute("error", "Failed to update status.Invalid status");
		verifyNoInteractions(model);
	}

	@Test
	void testShowParcelHistory() {
		when(parcelService.getRelatedParcelListOfCurrentRider()).thenReturn(Collections.singletonList(parcel));

		String view = riderController.showParcelHistory(model);

		assertEquals("rider/view-history", view);
		verify(model).addAttribute("parcels", Collections.singletonList(parcel));
		verify(parcelService).getRelatedParcelListOfCurrentRider();
	}
}
