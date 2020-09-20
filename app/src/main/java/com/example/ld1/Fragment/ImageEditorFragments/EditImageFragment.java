package com.example.ld1.Fragment.ImageEditorFragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.ld1.Interface.EditImageFragmentListener;
import com.example.ld1.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditImageFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private EditImageFragmentListener listener;
    private SeekBar seekbar_brightness,seekbar_contrant,seekbar_saturation;

    public void setListener(EditImageFragmentListener listener) {
        this.listener = listener;
    }

    public EditImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit_image, container, false);
        seekbar_brightness = view.findViewById(R.id.seekbar_brightness);
        seekbar_contrant = view.findViewById(R.id.seekbar_constrant);
        seekbar_saturation = view.findViewById(R.id.seekbar_saturation);

        seekbar_brightness.setMax(200);
        seekbar_brightness.setProgress(100);

        seekbar_contrant.setMax(200);
        seekbar_contrant.setProgress(100);

        seekbar_saturation.setMax(200);
        seekbar_saturation.setProgress(100);

        seekbar_saturation.setOnSeekBarChangeListener(this);
        seekbar_contrant.setOnSeekBarChangeListener(this);
        seekbar_brightness.setOnSeekBarChangeListener(this);

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(listener != null){
            if(seekBar.getId() == R.id.seekbar_brightness){
               listener.onBrightnessChanged(progress-100);
            }

           else if(seekBar.getId() == R.id.seekbar_constrant){
                progress+=10;
                float value = .010f*progress;
                listener.onConstrantChanged(value);
            }

           else if(seekBar.getId() == R.id.seekbar_saturation){
                float value = .010f*progress;
                listener.onSaturationChanged(value);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(listener != null){
            listener.onEditStarted();
        }

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(listener != null){
            listener.onEditCompleted();
        }
    }

    public void resetControls(){
        seekbar_brightness.setProgress(100);
        seekbar_contrant.setProgress(100);
        seekbar_saturation.setProgress(100);
    }


}
