package com.bytebreeze.quickdrop.controller;

import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
import com.bytebreeze.quickdrop.dto.request.UserProfileUpdateDto;
import com.bytebreeze.quickdrop.entity.ParcelEntity;
import com.bytebreeze.quickdrop.entity.ProductCategoryEntity;
import com.bytebreeze.quickdrop.repository.ProductCategoryRepository;
import com.bytebreeze.quickdrop.service.ParcelService;
import com.bytebreeze.quickdrop.service.SSLCommerzPaymentService;
import com.bytebreeze.quickdrop.service.UserService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	private MockMvc mockMvc;

	@Mock
	private UserService userService;

	@Mock
	private ProductCategoryRepository productCategoryRepository;

	@Mock
	private ParcelService parcelService;

	@Mock
	private SSLCommerzPaymentService sslCommerzPaymentService;

	@InjectMocks
	private UserController userController;

	private UserProfileUpdateDto userProfileUpdateDto;
	private ParcelBookingRequestDTO parcelBookingRequestDTO;
	private List<ParcelEntity> parcelEntities;
	private List<ProductCategoryEntity> productCategories;

	@BeforeEach
	void setUp() {
		// Initialize MockMvc with standalone setup
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

		// Initialize test data
		userProfileUpdateDto = new UserProfileUpdateDto();
		userProfileUpdateDto.setFullName("John Doe");
		userProfileUpdateDto.setPhoneNumber("01712345678");

		parcelBookingRequestDTO = new ParcelBookingRequestDTO();
		parcelBookingRequestDTO.setCategoryId(UUID.randomUUID());
		parcelBookingRequestDTO.setWeight(5.0);
		parcelBookingRequestDTO.setSize(10.0);
		parcelBookingRequestDTO.setPickupDivision("Dhaka");
		parcelBookingRequestDTO.setPickupDistrict("Dhaka");
		parcelBookingRequestDTO.setPickupUpazila("Gulshan");
		parcelBookingRequestDTO.setPickupVillage("Banani");
		parcelBookingRequestDTO.setReceiverName("Jane Doe");
		parcelBookingRequestDTO.setReceiverPhone("+8801712345678");
		parcelBookingRequestDTO.setReceiverDivision("Chittagong");
		parcelBookingRequestDTO.setReceiverDistrict("Chittagong");
		parcelBookingRequestDTO.setReceiverUpazila("Pahartali");
		parcelBookingRequestDTO.setReceiverVillage("Kattali");
		parcelBookingRequestDTO.setPrice(new BigDecimal("100.00"));
		parcelBookingRequestDTO.setDistance(200.0);
		parcelBookingRequestDTO.setPaymentMethod("card");
		parcelBookingRequestDTO.setTransactionId("TX123456");

		ParcelEntity parcel = new ParcelEntity();
		parcelEntities = Arrays.asList(parcel);

		ProductCategoryEntity category = new ProductCategoryEntity();
		productCategories = Arrays.asList(category);
	}

	@Test
	void userDashboard_ReturnsDashboardViewWithParcels() throws Exception {
		when(parcelService.getBookedButNotDeliveredParcels()).thenReturn(parcelEntities);

		mockMvc.perform(get("/user/dashboard"))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard/home"))
				.andExpect(model().attributeExists("bookedParcels"))
				.andExpect(model().attribute("bookedParcels", parcelEntities));

		verify(parcelService).getBookedButNotDeliveredParcels();
	}

	@Test
	void basicProfileSettings_ReturnsProfileSettingsViewWithDto() throws Exception {
		when(userService.userProfileUpdateGet()).thenReturn(userProfileUpdateDto);

		mockMvc.perform(get("/user/profile-settings"))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard/account"))
				.andExpect(model().attributeExists("userProfileUpdateDto"))
				.andExpect(model().attribute("userProfileUpdateDto", userProfileUpdateDto));

		verify(userService).userProfileUpdateGet();
	}

	@Test
	void updateAccountSettings_Success_RedirectsWithSuccessMessage() throws Exception {
		when(userService.updateUserProfile(any(UserProfileUpdateDto.class))).thenReturn(true);

		mockMvc.perform(post("/user/profile-settings")
						.param("fullName", "John Doe")
						.param("phoneNumber", "01712345678"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/profile-settings?success"))
				.andExpect(flash().attributeExists("successMessage"));

		verify(userService).updateUserProfile(any(UserProfileUpdateDto.class));
	}

	@Test
	void updateAccountSettings_ValidationError_ReturnsProfileSettingsView() throws Exception {
		mockMvc.perform(post("/user/profile-settings")
						.param("fullName", "") // Invalid: empty name
						.param("phoneNumber", "01712345678"))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard/account"))
				.andExpect(model().attributeHasFieldErrors("userProfileUpdateDto", "fullName"));

		verify(userService, never()).updateUserProfile(any(UserProfileUpdateDto.class));
	}

	@Test
	void updateAccountSettings_ServiceException_ReturnsProfileSettingsViewWithError() throws Exception {
		when(userService.updateUserProfile(any(UserProfileUpdateDto.class)))
				.thenThrow(new RuntimeException("Update failed"));

		mockMvc.perform(post("/user/profile-settings")
						.param("fullName", "John Doe")
						.param("phoneNumber", "01712345678"))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard/account"))
				.andExpect(model().attributeExists("updateError"))
				.andExpect(model().attribute(
								"updateError", "An error occurred while updating your profile. Please try again."));

		verify(userService).updateUserProfile(any(UserProfileUpdateDto.class));
	}

	@Test
	void bookParcel_ReturnsBookParcelViewWithAttributes() throws Exception {
		when(productCategoryRepository.findAll()).thenReturn(productCategories);

		mockMvc.perform(get("/user/book-parcel"))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard/book-parcel"))
				.andExpect(model().attributeExists("productCategories"))
				.andExpect(model().attribute("productCategories", productCategories))
				.andExpect(model().attributeExists("title"))
				.andExpect(model().attribute("title", "Book Parcel"))
				.andExpect(model().attributeExists("parcelBookingRequestDTO"))
				.andExpect(model().attribute("parcelBookingRequestDTO", isA(ParcelBookingRequestDTO.class)));

		verify(productCategoryRepository).findAll();
	}

	@Test
	void handleParcelBooking_ValidationError_ReturnsBookParcelView() throws Exception {
		when(productCategoryRepository.findAll()).thenReturn(productCategories);

		mockMvc.perform(post("/user/book-parcel")
						.param("categoryId", "") // Invalid: missing category
						.param("weight", "5.0")
						.param("size", "10.0")
						.param("pickupDivision", "Dhaka")
						.param("pickupDistrict", "Dhaka")
						.param("pickupUpazila", "Gulshan")
						.param("pickupVillage", "Banani")
						.param("receiverName", "Jane Doe")
						.param("receiverPhone", "+8801712345678")
						.param("receiverDivision", "Chittagong")
						.param("receiverDistrict", "Chittagong")
						.param("receiverUpazila", "Pahartali")
						.param("receiverVillage", "Kattali")
						.param("price", "100.00")
						.param("distance", "200.0")
						.param("paymentMethod", "card"))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard/book-parcel"))
				.andExpect(model().attributeExists("productCategories"))
				.andExpect(model().attributeExists("title"))
				.andExpect(model().attributeHasFieldErrors("parcelBookingRequestDTO", "categoryId"));

		verify(productCategoryRepository).findAll();
		verify(parcelService, never()).bookParcel(any(ParcelBookingRequestDTO.class));
	}

	@Test
	void getParcelHistory_ReturnsParcelHistoryViewWithParcels() throws Exception {
		when(parcelService.getParcelList()).thenReturn(parcelEntities);

		mockMvc.perform(get("/user/parcel-history"))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard/parcel-history"))
				.andExpect(model().attributeExists("parcelHistory"))
				.andExpect(model().attribute("parcelHistory", parcelEntities));

		verify(parcelService).getParcelList();
	}

	@Test
	void handleParcelBooking_ValidationError_ReturnsFormWithErrors() throws Exception {
		// Setup mock data
		when(productCategoryRepository.findAll()).thenReturn(productCategories);

		// Perform the request with invalid data (missing required fields)
		mockMvc.perform(post("/user/book-parcel")
						.param("weight", "5.0") // Missing other required fields
						.param("paymentMethod", "card"))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard/book-parcel"))
				.andExpect(model().attributeExists("productCategories"))
				.andExpect(model().attribute("productCategories", productCategories))
				.andExpect(model().attributeExists("title"))
				.andExpect(model().attributeHasErrors("parcelBookingRequestDTO"));

		// Verify no booking/payment processing occurred
		verify(parcelService, never()).generateTransactionId();
		verify(parcelService, never()).bookParcel(any());
		verify(sslCommerzPaymentService, never()).getPaymentUrl(any(), any());
	}
}
