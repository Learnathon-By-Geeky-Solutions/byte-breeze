//Controller:
@PostMapping("/Accept/{parcelId}")
public String acceptParcel(
        @PathVariable UUID parcelId,
        RedirectAttributes redirectAttributes) {

    try {
        riderService.acceptParcelDelivery(parcelId);
        redirectAttributes.addFlashAttribute("success", "Parcel accepted successfully!");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Failed to accept parcel: " + e.getMessage());
    }

    return "redirect:/rider/available-parcels";
}


// service
@Transactional
public void acceptParcelDelivery(UUID parcelId){

    Rider rider = getAuthenticatedRider();
    Parcel parcel = parcelRepository.findById(parcelId)
            .orElseThrow(() -> new ParcelNotFoundException("Parcel not found with ID: " + parcelId));

    if (parcel.getRider() != null) {
        throw new AlreadyExistsException("Parcel already assigned to another rider");
    }

    if(rider.getIsAssigned()){
        throw new ParcelAlreadyAssignedException("You have already assigned to a parcel");
    }
    rider.setIsAssigned(true);

    parcel.setRider(rider);
    parcel.setStatus(ParcelStatus.ASSIGNED);
    parcel.setAssignedAt(LocalDateTime.now());
    parcelRepository.save(parcel);

}

/// -------------------------------------
package com.bytebreeze.quickdrop.exception.custom;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class ParcelAlreadyAssignedException extends RuntimeException {
    public ParcelAlreadyAssignedException(String message) {
        super(message);
    }
}


//----------------------------

package com.bytebreeze.quickdrop.exception.custom;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)

public class ParcelNotFoundException extends RuntimeException {

    public ParcelNotFoundException(String message) {
        super(message);
    }

}


