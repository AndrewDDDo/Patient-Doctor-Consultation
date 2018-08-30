package com.group4.patientdoctorconsultation.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.group4.patientdoctorconsultation.common.FailableResource;
import com.group4.patientdoctorconsultation.common.LiveResultListener;
import com.group4.patientdoctorconsultation.data.model.Profile;
import com.group4.patientdoctorconsultation.data.repository.ProfileRepository;

public class ProfileViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final ProfileRepository profileRepository;
    private final MutableLiveData<Boolean> isSignedIn;

    private LiveData<FailableResource<Profile>> profile;

    ProfileViewModel(ProfileRepository profileRepository, FirebaseAuth firebaseAuth) {
        this.profileRepository = profileRepository;
        isSignedIn = new MutableLiveData<Boolean>() {
            @Override
            protected void onActive() {
                super.onActive();
                firebaseAuth.addAuthStateListener(ProfileViewModel.this);
            }

            @Override
            protected void onInactive() {
                super.onInactive();
                firebaseAuth.removeAuthStateListener(ProfileViewModel.this);
            }
        };
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        boolean signedIn = firebaseAuth.getCurrentUser() != null;

        isSignedIn.setValue(signedIn);

        if (signedIn) {
            profile = profileRepository.profileFromUserId(firebaseAuth.getCurrentUser().getUid());
        }
    }

    public LiveResultListener<Boolean> updateProfile(Profile profile) {
        return profileRepository.updateProfile(profile);
    }

    public LiveData<Boolean> getIsSignedIn() {
        return isSignedIn;
    }

    public LiveData<FailableResource<Profile>> getProfile() {
        return profile;
    }
}
