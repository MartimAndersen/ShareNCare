package pt.unl.fct.di.example.sharencare.user.main_menu.ui.profile;

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
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.common.CommonMethods;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.events.EventMethods;
import pt.unl.fct.di.example.sharencare.institution.login.InstitutionInfo;
import pt.unl.fct.di.example.sharencare.user.login.PointsData;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.main_menu.MainMenuUserActivity;
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

    private TextView username, email, mobile, landline, address, secondAddress, zipCode, zipCodeSecond, bio;

    private Switch publicProfile;

    public ChipGroup tags;

    private EditText editedEmail, editedMobile, editedLandline, editedAddress, editedSecondAddress, editedZipCode, editedZipCodeSecond, editedBio;

    private CircleImageView profilePic, editedProfilePic;

    private ImageButton changeAttributes, save, refresh;
    private boolean changing;

    private Repository profileRepository;
    private UserInfo user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, container, false);
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

        String userInfo = sharedpreferences.getString("USER", null);
        user = gson.fromJson(userInfo, UserInfo.class);

        profileRepository = profileRepository.getInstance();

        changing = false;
        image = null;

        profilePic = getView().findViewById(R.id.profile_image);
        editedProfilePic = getView().findViewById(R.id.profile_image_edit);

        username = getView().findViewById(R.id.username);
        email = getView().findViewById(R.id.email);
        address = getView().findViewById(R.id.address);
        secondAddress = getView().findViewById(R.id.second_address);
        mobile = getView().findViewById(R.id.mobile);
        landline = getView().findViewById(R.id.landline);
        zipCode = getView().findViewById(R.id.zip);
        zipCodeSecond = getView().findViewById(R.id.zip_second);
        bio = getView().findViewById(R.id.bio);

        publicProfile = getView().findViewById(R.id.fragment_profile_public);

        tags = getView().findViewById(R.id.fragment_profile_tags);

        editedEmail = getView().findViewById(R.id.fragment_profile_email);
        editedAddress = getView().findViewById(R.id.fragment_profile_address);
        editedSecondAddress = getView().findViewById(R.id.fragment_profile_second_address);
        editedMobile = getView().findViewById(R.id.fragment_profile_mobile);
        editedLandline = getView().findViewById(R.id.fragment_profile_landline);
        editedZipCode = getView().findViewById(R.id.fragment_profile_zip_code);
        editedZipCodeSecond = getView().findViewById(R.id.fragment_profile_zip_code_second);
        editedBio = getView().findViewById(R.id.fragment_profile_bio);

        changeAttributes = getView().findViewById(R.id.edit_attributes);
        save = getView().findViewById(R.id.save_changes);
        refresh = getView().findViewById(R.id.refresh);

        CommonMethods.setTagsAndCheck(getContext(), tags, user.getTags());
        setAttributes();
        setTextProperties();

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
                changing = true;
                changeAttributes.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);

                email.setVisibility(View.GONE);
                editedEmail.setVisibility(View.VISIBLE);

                address.setVisibility(View.INVISIBLE);
                editedAddress.setVisibility(View.VISIBLE);

                secondAddress.setVisibility(View.INVISIBLE);
                editedSecondAddress.setVisibility(View.VISIBLE);

                mobile.setVisibility(View.INVISIBLE);
                editedMobile.setVisibility(View.VISIBLE);

                landline.setVisibility(View.INVISIBLE);
                editedLandline.setVisibility(View.VISIBLE);

                zipCode.setVisibility(View.INVISIBLE);
                editedZipCode.setVisibility(View.VISIBLE);

                zipCodeSecond.setVisibility(View.INVISIBLE);
                editedZipCodeSecond.setVisibility(View.VISIBLE);

                bio.setVisibility(View.INVISIBLE);
                editedBio.setVisibility(View.VISIBLE);

                profilePic.setVisibility(View.INVISIBLE);
                editedProfilePic.setVisibility(View.VISIBLE);
            }
        });

        save.setOnClickListener(v -> {
            save.setVisibility(View.INVISIBLE);
            changeAttributes.setVisibility(View.VISIBLE);
            if(changing){
                publicProfile.setEnabled(true);
                String profileType;
                if(publicProfile.isChecked())
                    profileType = "public";
                else
                    profileType = "private";

                String newEmail = editedEmail.getText().toString();
                String newMobile = editedMobile.getText().toString();
                String newLandLine = editedLandline.getText().toString();
                String newAddress = editedAddress.getText().toString();
                String newSecondAddress = editedSecondAddress.getText().toString();
                String newZipCode = editedZipCode.getText().toString();
                String newZipCodeSecond = editedZipCodeSecond.getText().toString();
                String newBio = editedBio.getText().toString();

                String finalZip;

                if(newZipCode.equals("") && newZipCodeSecond.equals(""))
                    finalZip = "";
                else
                    finalZip = newZipCode + "-" + newZipCodeSecond;

            ProfileUser u = new ProfileUser(
                    newAddress,
                    newBio,
                    newEmail,
                    null,
                    newLandLine,
                    newMobile,
                    image,
                    profileType,
                    newSecondAddress,
                    EventMethods.getTags(tags),
                    finalZip
            );

            profileRepository.getProfileService().changeProfile(user.getToken(), u).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                    if(r.isSuccessful()){
                        Toast.makeText(getContext(), "Managed to change Attributes", Toast.LENGTH_SHORT).show();

                        String json = sharedpreferences.getString("USER", null);
                        UserInfo user = gson.fromJson(json, UserInfo.class);

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


                            user.setSecondAddress(newSecondAddress);
                            secondAddress.setText(newSecondAddress);
                            editedEmail.setText(newEmail);

                        editedSecondAddress.setVisibility(View.INVISIBLE);
                        secondAddress.setVisibility(View.VISIBLE);


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

                        user.setZipCode(newZipCode + "-" + newZipCodeSecond);
                        zipCode.setText(newZipCode);
                        zipCodeSecond.setText(newZipCodeSecond);
                        editedZipCode.setText(newZipCode);
                        editedZipCodeSecond.setText(newZipCodeSecond);

                        editedZipCode.setVisibility(View.INVISIBLE);
                        editedZipCodeSecond.setVisibility(View.INVISIBLE);
                        zipCode.setVisibility(View.VISIBLE);
                        zipCodeSecond.setVisibility(View.VISIBLE);

                            user.setBio(newBio);
                            bio.setText(newBio);
                            editedBio.setText(newBio);

                        editedBio.setVisibility(View.INVISIBLE);
                        bio.setVisibility(View.VISIBLE);

                        editedProfilePic.setVisibility(View.INVISIBLE);
                        profilePic.setVisibility(View.VISIBLE);

                        if(user.getProfileType().equals("private") && publicProfile.isChecked())
                            user.setProfileType("public");

                        user.setTags(EventMethods.getTags(tags));

                        SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                        String u = gson.toJson(user);
                        prefsEditor.putString("USER", u);
                        prefsEditor.apply();

                    }
                    else {
                        Toast.makeText(getContext(), "CODE: "+r.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), "FAIL", Toast.LENGTH_SHORT).show();
                }
            });

        }});

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInfo = sharedpreferences.getString("USER", null);
                UserInfo user = gson.fromJson(userInfo, UserInfo.class);

                profileRepository.getProfileService().getUser(user.getToken(), user.getUsername()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful()){
                            saveUser(r);
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
                user.setProfilePic(bitmap);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                String heading = new String(Base64.encodeToString(out.toByteArray(),Base64.DEFAULT));

                SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                prefsEditor.putString("PIC",heading);
                prefsEditor.apply();

                image = out.toByteArray();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void setAttributes(){
        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);
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
        }if(!user.getSecondAddress().equals("")) {
            secondAddress.setText(user.getSecondAddress());
            editedSecondAddress.setText(user.getSecondAddress());
        }if(!user.getZipCode().equals("-") &&!user.getZipCode().equals("")) {
            String[] split = user.getZipCode().split("-");
            zipCode.setText(split[0]);
            zipCodeSecond.setText(split[1]);
            editedZipCode.setText(split[0]);
            editedZipCodeSecond.setText(split[1]);
        }if(!user.getBio().equals("")) {
            bio.setText(user.getBio());
            editedBio.setText(user.getBio());
        }

        List<Integer> t = user.getTags();

        if(user.profileType.equals("public"))
            publicProfile.setChecked(true);

        String profilePicString = sharedpreferences.getString("PIC", null);

        if(profilePicString == null)
            getProfilePic(user.getToken());
       else {
            byte[] decode = Base64.decode(profilePicString.getBytes(), 1);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            profilePic.setImageBitmap(bitmap);
            editedProfilePic.setImageBitmap(bitmap);
        }

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
    
    private void setTextProperties(){
        username.setMaxLines(1);
        username.setEllipsize(TextUtils.TruncateAt.END);
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getLineCount() > 1)
                    username.setMaxLines(1);
                else if(username.length() != 0)
                    username.setMaxLines(username.length());
            }
        });

        email.setMaxLines(1);
        email.setEllipsize(TextUtils.TruncateAt.END);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getLineCount() > 1)
                    email.setMaxLines(1);
                else if(email.length() != 0)
                    email.setMaxLines(email.length());

            }
        });

        address.setMaxLines(1);
        address.setEllipsize(TextUtils.TruncateAt.END);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(address.getLineCount() > 1)
                    address.setMaxLines(1);
                 else if(address.length() != 0)
                    address.setMaxLines(address.length());
            }
        });

        secondAddress.setMaxLines(1);
        secondAddress.setEllipsize(TextUtils.TruncateAt.END);
        secondAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(secondAddress.getLineCount() > 1)
                    secondAddress.setMaxLines(1);
                else if(secondAddress.length() != 0)
                    secondAddress.setMaxLines(secondAddress.length());
            }
        });

        mobile.setMaxLines(1);
        mobile.setEllipsize(TextUtils.TruncateAt.END);
        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mobile.getLineCount() > 1)
                    mobile.setMaxLines(1);
                else if(mobile.length() != 0)
                    mobile.setMaxLines(mobile.length());
            }
        });

        landline.setMaxLines(1);
        landline.setEllipsize(TextUtils.TruncateAt.END);
        landline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(landline.getLineCount() > 1)
                    landline.setMaxLines(1);
                else if(landline.length() != 0)
                    landline.setMaxLines(landline.length());
            }
        });

        zipCode.setMaxLines(1);
        zipCode.setEllipsize(TextUtils.TruncateAt.END);
        zipCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(zipCode.getLineCount() > 1)
                    zipCode.setMaxLines(1);
                else if(zipCode.length() != 0)
                    zipCode.setMaxLines(zipCode.length());
            }
        });

        bio.setMaxLines(5);
        bio.setEllipsize(TextUtils.TruncateAt.END);
        bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bio.getLineCount() > 5)
                    bio.setMaxLines(5);
                else if(bio.length() != 0)
                    bio.setMaxLines(bio.length());
            }
        });
    }

    public void saveUser(Response<ResponseBody> r){
        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        if (r.isSuccessful()) {
            try {
                JSONArray array = new JSONArray(r.body().string());
                List<LinkedTreeMap> list = gson.fromJson(array.toString(), List.class);
                List<String> values = new ArrayList<String>();
                for(int i = 0; i < list.size(); i++)
                    values.add(list.get(i).get("value").toString());

                user.setAddress(values.get(0));
                user.setBio(values.get(1));
                user.setEmail(values.get(2));
                user.setEvents(getEvents(values.get(3)));
                user.setLandLine(values.get(4));
                user.setMobile(values.get(5));
                user.setMyTracks(getTracks(values.get(6)));
                user.setPoints(getPoints(values.get(8)));
                user.setProfileType(values.get(9));
                user.setSecondAddress(values.get(11));
                user.setTags(getTags(values.get(13)));
                user.setUsername(values.get(14));
                user.setZipCode(values.get(15));

                SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                String json = gson.toJson(user);
                prefsEditor.putString("USER", json);
                prefsEditor.apply();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(getContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
    }

    private List<Integer> getTags(String tags){
        return gson.fromJson(tags, List.class);
    }

    private List<String> getEvents(String events){
        return gson.fromJson(events, List.class);
    }

    private List<String> getTracks(String tracks){
        return gson.fromJson(tracks, List.class);
    }

    private PointsData getPoints(String points){
        Type t = new TypeToken<PointsData>(){}.getType();
        return gson.fromJson(points, t);
    }

}