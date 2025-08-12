package com.seettu.backend.service;

import com.seettu.backend.dto.CreatePackageRequest;
import com.seettu.backend.entity.SeettuPackage;
import com.seettu.backend.entity.User;
import com.seettu.backend.repository.SeettuPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeettuPackageService {
    
    @Autowired
    private SeettuPackageRepository packageRepository;
    
    public SeettuPackage createPackage(CreatePackageRequest request, User provider) {
        SeettuPackage seettuPackage = new SeettuPackage();
        seettuPackage.setPackageName(request.getPackageName());
        seettuPackage.setDescription(request.getDescription());
        seettuPackage.setPackageValue(request.getPackageValue());
        seettuPackage.setProvider(provider);
        seettuPackage.setIsActive(true);
        
        return packageRepository.save(seettuPackage);
    }
    
    public List<SeettuPackage> getPackagesByProvider(User provider) {
        return packageRepository.findByProviderAndIsActiveTrue(provider);
    }
    
    public List<SeettuPackage> getAllActivePackages() {
        return packageRepository.findByIsActiveTrue();
    }
    
    public SeettuPackage getPackageById(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found"));
    }
    
    public void deletePackage(Long id) {
        SeettuPackage seettuPackage = getPackageById(id);
        seettuPackage.setIsActive(false);
        packageRepository.save(seettuPackage);
    }
}