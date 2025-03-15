package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;

public interface PaymentService {
    /*
    * This method will initiate payment and fetch the payment url
    * Then return the url to controller layer
     */
    String getPaymentUrl(ParcelBookingRequestDTO parcelBookingRequestDTO);

}
