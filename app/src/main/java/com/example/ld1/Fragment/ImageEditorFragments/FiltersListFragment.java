package com.example.ld1.Fragment.ImageEditorFragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ld1.Adapter.ThumbnailAdapter;
import com.example.ld1.FilterPostActivity;
import com.example.ld1.Interface.FiltersListFragmentListener;
import com.example.ld1.MainActivity;
import com.example.ld1.R;
import com.example.ld1.Utilities.BitmapUtils;
import com.example.ld1.Utilities.SpacesItemDecoration;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersListFragment extends Fragment implements FiltersListFragmentListener {

    RecyclerView recyclerView;
    ThumbnailAdapter adapter;
    List<ThumbnailItem>  thumbnailItems;

    FiltersListFragmentListener listener;

    public void setListener(FiltersListFragmentListener listener) {
        this.listener = listener;
    }

    public FiltersListFragment() {
        // Required empty public constructor
    }

    Bitmap original;

    public FiltersListFragment(Bitmap bitmap) {
        // Required empty public constructor
        original=bitmap;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filters_list, container, false);


        thumbnailItems = new ArrayList<ThumbnailItem>();
        adapter = new ThumbnailAdapter(thumbnailItems,this,getActivity());

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_filter_list);
        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20,getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        recyclerView.setAdapter(adapter);



        displayThumbnail(original);

        return view;
    }

    public void displayThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                      Bitmap thumbImg;
                      if(bitmap == null){
                          thumbImg = BitmapUtils.getBitmapFromAssets(getActivity(), FilterPostActivity.pictureName,100,100);
                      }
                      else{
                          thumbImg = Bitmap.createScaledBitmap(bitmap,100,100,true);
                      }

                      if(thumbImg==null){
                          return;
                      }
                      ThumbnailsManager.clearThumbs();

                      thumbnailItems.clear();

                      //add normal bitmap first
                      ThumbnailItem thumbnailItem = new ThumbnailItem();
                      thumbnailItem.image = thumbImg;
                      thumbnailItem.filterName= "Normal";
                      ThumbnailsManager.addThumb(thumbnailItem);

                      List<Filter> filters = FilterPack.getFilterPack(FiltersListFragment.this.getActivity());

                      for(Filter filter: filters){
                          ThumbnailItem t1 = new ThumbnailItem();
                          t1.image = thumbImg;
                          t1.filter = filter;
                          t1.filterName = filter.getName();
                          ThumbnailsManager.addThumb(t1);
                      }


                      thumbnailItems.addAll(ThumbnailsManager.processThumbs(getActivity()));

                      getActivity().runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              adapter.notifyDataSetChanged();
                          }
                      });

            }
        };
        new Thread(r).start();
    }

    @Override
    public void onFilterSelected(Filter filter) {

        if(listener !=null){
            listener.onFilterSelected(filter);
        }

    }
}
