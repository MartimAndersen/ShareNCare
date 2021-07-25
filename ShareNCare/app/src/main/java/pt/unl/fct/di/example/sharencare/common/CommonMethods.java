package pt.unl.fct.di.example.sharencare.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import pt.unl.fct.di.example.sharencare.R;

import static com.google.gson.internal.$Gson$Types.arrayOf;

public class CommonMethods {
    public static String[] tagNames = {"Animals", "Environment", "Children", "Elderly", "Supplies", "Homeless"};
    public static int[] tagIcons = {R.drawable.animals_black, R.drawable.environment_black, R.drawable.children_black, R.drawable.elderly_black, R.drawable.supplies_black, R.drawable.homeless_black};

    public static void setTags(Context context, ChipGroup c){
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i = 0; i < tagNames.length; i++) {
            Chip chip = new Chip(context);
            chip.setText(tagNames[i]);
            chip.setChipIcon(context.getResources().getDrawable(tagIcons[i], context.getTheme()));
            chip.setCheckable(true);
            chip.setChipBackgroundColor(buildColorStateList(context,"#8BC34A","#DCDCDC"));
            c.addView(chip);
        }
    }

    public static ColorStateList buildColorStateList(Context context, String pressedColorAttr, String defaultColorAttr){
        int pressedColor = Color.parseColor(pressedColorAttr);
        int defaultColor = Color.parseColor(defaultColorAttr);

        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{} // this should be empty to make default color as we want
                }, new int[]{
                pressedColor,
                defaultColor
        }
        );
    }
    public static void setTagsAndCheck(Context context, ChipGroup c, List<Integer> t){
        for(int i = 0; i < tagNames.length; i++) {
            Chip chip = new Chip(context);
            chip.setText(tagNames[i]);
            chip.setChipIcon(context.getResources().getDrawable(tagIcons[i], context.getTheme()));
            chip.setCheckable(true);
            chip.setChipBackgroundColor(buildColorStateList(context,"#8BC34A","#DCDCDC"));
            c.addView(chip);
        }

            for(int j = 0; j < t.size(); j++) {
                Chip chip2 = (Chip) c.getChildAt(t.get(j));
                c.check(chip2.getId());
            }
    }


}
