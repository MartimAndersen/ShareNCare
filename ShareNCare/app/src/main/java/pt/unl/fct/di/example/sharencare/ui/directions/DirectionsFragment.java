package pt.unl.fct.di.example.sharencare.ui.directions;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.Repository;
import pt.unl.fct.di.example.sharencare.databinding.FragmentDirectionsBinding;
import pt.unl.fct.di.example.sharencare.databinding.FragmentDirectionsBinding;
import pt.unl.fct.di.example.sharencare.login.LoginUser;
import pt.unl.fct.di.example.sharencare.map.TrackData;
import pt.unl.fct.di.example.sharencare.ui.home.HomeFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionsFragment extends Fragment {

    private DirectionsViewModel directionsViewModel;
    private FragmentDirectionsBinding binding;
    private EditText title, description, origin, destination;
    private Button register;
    private Repository directionsRepository;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        title = getActivity().findViewById(R.id.fragment_directions_title);
        description = getActivity().findViewById(R.id.fragment_directions_description);
        origin = getActivity().findViewById(R.id.fragment_directions_origin);
        destination = getActivity().findViewById(R.id.fragment_directions_destination);
        register = getActivity().findViewById(R.id.fragment_directions_register);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        directionsRepository = directionsRepository.getInstance();

        register.setOnClickListener(v -> {
          //  getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);
            TrackData t = new TrackData(
                    title.getText().toString(),
                    description.getText().toString(),
                    origin.getText().toString(),
                    destination.getText().toString()
            );

            directionsRepository.getMapService().registerTrack(t).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                 //   getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
                    if (r.isSuccessful()) {
                        HomeFragment nextFrag= new HomeFragment();

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(((ViewGroup)getView().getParent()).getId(), nextFrag, "findThisFragment")
                                .addToBackStack(null)
                                .commit();
                    } else {
                      //  findViewById(R.id.wrong_credentials).setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Error creating track", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), "Error Logging User: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
            return;

        });
    }
}