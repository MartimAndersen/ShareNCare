package pt.unl.fct.di.example.sharencare.institution.main_menu.ui.directions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DirectionsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DirectionsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}