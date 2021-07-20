package pt.unl.fct.di.example.sharencare.institution.main_menu.ui.new_event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewEventViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NewEventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}