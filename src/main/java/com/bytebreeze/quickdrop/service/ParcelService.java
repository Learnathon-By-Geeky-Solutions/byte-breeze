package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.model.Parcel;
import com.bytebreeze.quickdrop.repository.ParcelRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ParcelService {
    private final ParcelRepository parcelRepository;

    public ParcelService(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    public Parcel saveParcel(Parcel parcel) {
        return parcelRepository.save(parcel);
    }

    public List<Parcel> getAllParcels() {
        return parcelRepository.findAll(); // Fetch all parcel data from the database
    }
}
