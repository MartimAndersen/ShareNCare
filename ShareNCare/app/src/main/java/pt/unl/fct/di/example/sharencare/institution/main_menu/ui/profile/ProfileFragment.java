package pt.unl.fct.di.example.sharencare.institution.main_menu.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.institution.login.InstitutionInfo;
import pt.unl.fct.di.example.sharencare.institution.main_menu.MainMenuInstitutionActivity;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.login.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private SharedPreferences sharedpreferences;
    private ProfileViewModel mViewModel;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    Gson gson = new Gson();;

    private byte[] image;

    private TextView username, email, mobile, landline, address,
            zipCode, website, instagram, youtube, twitter, facebook, fax, bio;
    private EditText editedEmail, editedMobile, editedLandline, editedAddress,
            editedZipCode, editedWebsite, editedInstagram, editedYoutube, editedTwitter, editedFacebook, editedFax, editedBio;

    private ImageView profilePic, editedProfilePic;

    private ImageButton changeAttributes;
    private boolean changing;

    private ImageButton save, refresh;

    private Repository profileRepository;
    private InstitutionInfo user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile_institution, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        sharedpreferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        String institutionInfo = sharedpreferences.getString("USER", null);
        user = gson.fromJson(institutionInfo, InstitutionInfo.class);

        profileRepository = profileRepository.getInstance();

        changing = false;

        profilePic = getView().findViewById(R.id.fragment_profile_institution_logo);
        editedProfilePic = getView().findViewById(R.id.fragment_profile_institution_logo_edit);

        username = getView().findViewById(R.id.fragment_profile_institutions_username);
        email = getView().findViewById(R.id.fragment_profile_institution_email);
        address = getView().findViewById(R.id.fragment_profile_institutions_address);
        mobile = getView().findViewById(R.id.fragment_profile_institution_mobile);
        landline = getView().findViewById(R.id.fragment_profile_institution_landline);
        zipCode = getView().findViewById(R.id.fragment_profile_institution_zip);
        website = getView().findViewById(R.id.fragment_profile_institution_website);
        instagram = getView().findViewById(R.id.fragment_profile_institution_instagram);
        youtube = getView().findViewById(R.id.fragment_profile_institution_youtube);
        twitter = getView().findViewById(R.id.fragment_profile_institution_twitter);
        facebook = getView().findViewById(R.id.fragment_profile_institution_facebook);
        fax = getView().findViewById(R.id.fragment_profile_institution_fax);
        bio = getView().findViewById(R.id.fragment_profile_institution_bio);

        editedEmail = getView().findViewById(R.id.fragment_profile_institution_edit_email);
        editedAddress = getView().findViewById(R.id.fragment_profile_institution_edit_address);
        editedMobile = getView().findViewById(R.id.fragment_profile_institution_edit_mobile);
        editedLandline = getView().findViewById(R.id.fragment_profile_institution_edit_landline);
        editedZipCode = getView().findViewById(R.id.fragment_profile_institution_edit_zip);
        editedInstagram = getView().findViewById(R.id.fragment_profile_institution_edit_instagram);
        editedFacebook = getView().findViewById(R.id.fragment_profile_institution_edit_facebook);
        editedTwitter = getView().findViewById(R.id.fragment_profile_institution_edit_twitter);
        editedYoutube = getView().findViewById(R.id.fragment_profile_institution_edit_youtube);
        editedWebsite = getView().findViewById(R.id.fragment_profile_institution_edit_website);
        editedFax = getView().findViewById(R.id.fragment_profile_institution_edit_fax);
        editedBio = getView().findViewById(R.id.fragment_profile_institution_edit_bio);

        changeAttributes = getView().findViewById(R.id.fragment_profile_institution_edit_attributes);
        refresh = getView().findViewById(R.id.refresh_institution);

        setAttributes();

        editedProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
            }
        });

        changeAttributes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAttributes.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
                changing = true;

                email.setVisibility(View.GONE);
                editedEmail.setVisibility(View.VISIBLE);

                address.setVisibility(View.INVISIBLE);
                editedAddress.setVisibility(View.VISIBLE);

                bio.setVisibility(View.INVISIBLE);
                editedBio.setVisibility(View.VISIBLE);

                mobile.setVisibility(View.INVISIBLE);
                editedMobile.setVisibility(View.VISIBLE);

                landline.setVisibility(View.INVISIBLE);
                editedLandline.setVisibility(View.VISIBLE);

                zipCode.setVisibility(View.INVISIBLE);
                editedZipCode.setVisibility(View.VISIBLE);

                instagram.setVisibility(View.INVISIBLE);
                editedInstagram.setVisibility(View.VISIBLE);

                facebook.setVisibility(View.INVISIBLE);
                editedFacebook.setVisibility(View.VISIBLE);

                twitter.setVisibility(View.INVISIBLE);
                editedTwitter.setVisibility(View.VISIBLE);

                youtube.setVisibility(View.INVISIBLE);
                editedYoutube.setVisibility(View.VISIBLE);

                website.setVisibility(View.INVISIBLE);
                editedWebsite.setVisibility(View.VISIBLE);

                fax.setVisibility(View.INVISIBLE);
                editedFax.setVisibility(View.VISIBLE);

                profilePic.setVisibility(View.INVISIBLE);
                editedProfilePic.setVisibility(View.VISIBLE);
            }
        });

        save = getView().findViewById(R.id.fragment_profile_institution_save);
        save.setOnClickListener(v -> {
            save.setVisibility(View.INVISIBLE);
            changeAttributes.setVisibility(View.VISIBLE);
            if(changing){

                String newEmail = editedEmail.getText().toString();
                String newMobile = editedMobile.getText().toString();
                String newLandLine = editedLandline.getText().toString();
                String newAddress = editedAddress.getText().toString();
                String newZipCode = editedZipCode.getText().toString();
                String newInstagram = editedInstagram.getText().toString();
                String newFacebook = editedFacebook.getText().toString();
                String newTwitter = editedTwitter.getText().toString();
                String newYoutube = editedYoutube.getText().toString();
                String newWebsite = editedWebsite.getText().toString();
                String newFax = editedFax.getText().toString();
                String newBio = editedBio.getText().toString();

               ProfileInstitution ins = new ProfileInstitution(
                    newAddress,
                    newBio,
                    newEmail,
                    null,
                    newFacebook,
                    newFax,
                    newInstagram,
                    newLandLine,
                    newMobile,
                    image,
                    newTwitter,
                    newWebsite,
                    newYoutube,
                    newZipCode
               );

                profileRepository.getProfileService().changeProfileInstitution(user.getToken(), ins).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful()){
                            Toast.makeText(getContext(), "Managed to change Attributes", Toast.LENGTH_SHORT).show();

                            String json = sharedpreferences.getString("USER", null);
                            InstitutionInfo user = gson.fromJson(json, InstitutionInfo.class);

                                user.setEmail(newEmail);
                                email.setText(newEmail);
                                editedEmail.setText(newEmail);

                            editedEmail.setVisibility(View.INVISIBLE);
                            email.setVisibility(View.VISIBLE);

                                user.setAddress(newAddress);
                                address.setText(newAddress);
                                editedAddress.setText(newAddress);

                            editedAddress.setVisibility(View.INVISIBLE);
                            address.setVisibility(View.VISIBLE);

                                user.setMobile(newMobile);
                                mobile.setText(newMobile);
                                editedMobile.setText(newMobile);

                            editedMobile.setVisibility(View.INVISIBLE);
                            mobile.setVisibility(View.VISIBLE);

                                user.setLandLine(newLandLine);
                                landline.setText(newLandLine);
                                editedLandline.setText(newLandLine);

                            editedLandline.setVisibility(View.INVISIBLE);
                            landline.setVisibility(View.VISIBLE);

                                user.setZipCode(newZipCode);
                                zipCode.setText(newZipCode);
                                editedZipCode.setText(newZipCode);

                            editedZipCode.setVisibility(View.INVISIBLE);
                            zipCode.setVisibility(View.VISIBLE);

                                user.setInstagram(newInstagram);
                                instagram.setText(newInstagram);
                                editedInstagram.setText(newInstagram);

                            editedInstagram.setVisibility(View.INVISIBLE);
                            instagram.setVisibility(View.VISIBLE);

                                user.setFacebook(newFacebook);
                                facebook.setText(newFacebook);
                                editedFacebook.setText(newFacebook);

                            editedFacebook.setVisibility(View.INVISIBLE);
                            facebook.setVisibility(View.VISIBLE);

                                user.setTwitter(newTwitter);
                                twitter.setText(newTwitter);
                                editedTwitter.setText(newTwitter);

                            editedTwitter.setVisibility(View.INVISIBLE);
                            twitter.setVisibility(View.VISIBLE);

                                user.setYoutube(newYoutube);
                                youtube.setText(newYoutube);
                                editedYoutube.setText(newYoutube);

                            editedYoutube.setVisibility(View.INVISIBLE);
                            youtube.setVisibility(View.VISIBLE);

                                user.setWebsite(newWebsite);
                                website.setText(newWebsite);
                                editedWebsite.setText(newWebsite);

                            editedWebsite.setVisibility(View.INVISIBLE);
                            website.setVisibility(View.VISIBLE);

                                user.setFax(newFax);
                                fax.setText(newFax);
                                editedFax.setText(newFax);

                            editedFax.setVisibility(View.INVISIBLE);
                            fax.setVisibility(View.VISIBLE);

                                user.setBio(newBio);
                                bio.setText(newBio);
                                editedBio.setText(newBio);

                            editedBio.setVisibility(View.INVISIBLE);
                            bio.setVisibility(View.VISIBLE);

                            editedProfilePic.setVisibility(View.INVISIBLE);
                            profilePic.setVisibility(View.VISIBLE);

                            SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                            String u = gson.toJson(user);
                            prefsEditor.putString("USER", u);
                            prefsEditor.apply();

                        }
                        else {
                            Toast.makeText(getContext(), "error code: "+r.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                    }
                });

            }});

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String institutionInfo = sharedpreferences.getString("USER", null);
                InstitutionInfo user = gson.fromJson(institutionInfo, InstitutionInfo.class);

                profileRepository.getProfileService().getUser(user.getToken(), user.getUsername()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful()){
                            saveInstitution(r);
                            setAttributes();
                        } else
                            Toast.makeText(getContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "FAIL", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK){
            imageUri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                editedProfilePic.setImageBitmap(bitmap);
                profilePic.setImageBitmap(bitmap);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                image = out.toByteArray();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void setAttributes(){
        String institutionInfo = sharedpreferences.getString("USER", null);
        InstitutionInfo user = gson.fromJson(institutionInfo, InstitutionInfo.class);
        if(!user.getUsername().equals(""))
            username.setText(user.getUsername());
        if(!user.getEmail().equals("")) {
            email.setText(user.getEmail());
            editedEmail.setText(user.getEmail());
        }if(!user.getMobile().equals("")) {
            mobile.setText(user.getMobile());
            editedMobile.setText(user.getMobile());
        }if(!user.getLandLine().equals("")) {
            landline.setText(user.getLandLine());
            editedLandline.setText(user.getLandLine());
        }if(!user.getAddress().equals("")) {
            address.setText(user.getAddress());
            editedAddress.setText(user.getAddress());
        }if(!user.getZipCode().equals("")) {
            zipCode.setText(user.getZipCode());
            editedZipCode.setText(user.getZipCode());
        }if(!user.getInstagram().equals("")) {
            instagram.setText(user.getInstagram());
            editedInstagram.setText(user.getInstagram());
        }if(!user.getFacebook().equals("")) {
            facebook.setText(user.getFacebook());
            editedFacebook.setText(user.getFacebook());
        }if(!user.getTwitter().equals("")) {
            twitter.setText(user.getTwitter());
            editedTwitter.setText(user.getTwitter());
        }if(!user.getYoutube().equals("")) {
            youtube.setText(user.getYoutube());
            editedYoutube.setText(user.getYoutube());
        }if(!user.getWebsite().equals("")) {
            website.setText(user.getWebsite());
            editedWebsite.setText(user.getWebsite());
        }if(!user.getFax().equals("")) {
            fax.setText(user.getFax());
            editedFax.setText(user.getFax());
        }if(!user.getBio().equals("")){
            bio.setText(user.getBio());
            editedBio.setText(user.getBio());
        }

        String profilePicString = sharedpreferences.getString("PIC", null);

        if(profilePicString == null)
            getProfilePic(user.getToken());
        else {
            byte[] decode = Base64.decode(profilePicString.getBytes(), 1);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            profilePic.setImageBitmap(bitmap);
            editedProfilePic.setImageBitmap(bitmap);
        }
        getProfilePic(user.getToken());

    }

    private void getProfilePic(List<String> token){
        profileRepository.getProfileService().getProfilePic(token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful()){
                    try {
                        String body = r.body().string();
                        Type t = new TypeToken<byte[]>(){}.getType();
                        byte[] byteArray = gson.fromJson(r.body().string(),t);
                        if(byteArray != null && !body.equals("")) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                            profilePic.setImageBitmap(bitmap);
                            editedProfilePic.setImageBitmap(bitmap);

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                            String headimg = new String(Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT));

                            SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                            prefsEditor.putString("PIC",headimg);
                            prefsEditor.apply();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else{
                    Toast.makeText(getContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT);
            }
        });
    }

    public void saveInstitution(Response<ResponseBody> r){
        if (r.isSuccessful()) {
            try {
                JSONArray array = new JSONArray(r.body().string());
                List<LinkedTreeMap> list = gson.fromJson(array.toString(), List.class);
                List<String> values = new ArrayList<String>();
                for(int i = 0; i < list.size(); i++)
                    values.add(list.get(i).get("value").toString());

                InstitutionInfo ins = new InstitutionInfo(
                        values.get(0),
                        values.get(1),
                        values.get(2),
                        values.get(3),
                        getEvents(values.get(4)),
                        values.get(5),
                        values.get(6),
                        values.get(7),
                        values.get(8),
                        values.get(9),
                        values.get(10),
                        values.get(14),
                        values.get(15),
                        values.get(16),
                        values.get(17),
                        values.get(18),
                        r.headers().values("Set-Cookie")
                );

                SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                String json = gson.toJson(ins);
                prefsEditor.putString("USER", json);
                prefsEditor.apply();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(getContext(), "FAILED: " + r.code(), Toast.LENGTH_SHORT).show();
    }

    private List<String> getEvents(String events){
        return gson.fromJson(events, List.class);
    }


}