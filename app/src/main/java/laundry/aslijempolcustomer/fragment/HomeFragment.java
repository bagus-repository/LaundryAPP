package laundry.aslijempolcustomer.fragment;

import android.content.Context;

import laundry.aslijempolcustomer.activity.OrderActivity;
import laundry.aslijempolcustomer.model.Image;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.SessionManager;
import laundry.aslijempolcustomer.utils.Tools;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TextView txtFullname;
    SessionManager sessionManager;
    LinearLayout layout_dots;
    AdapterImageSlider adapterImageSlider;
    Runnable runnable = null;
    Handler handler = new Handler();
    ViewPager viewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(getContext());
        txtFullname = rootView.findViewById(R.id.txtfullname);
        txtFullname.setText(sessionManager.getFullname());

        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OrderActivity.class);
                startActivity(intent);
            }
        });

//        initSliderImage(rootView);
        layout_dots = rootView.findViewById(R.id.layout_dots);
        viewPager = rootView.findViewById(R.id.pager);
        adapterImageSlider = new AdapterImageSlider(this, new ArrayList<Image>());

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_GET_IMAGE_SLIDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Image Slider response: ", response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray("sliders");
                        if (jsonArray.length() > 0) {
                            final List<Image> items = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Image obj = new Image();
                                obj.image_title = jsonArray.getJSONObject(i).getString("image_title");
                                obj.image_desc = jsonArray.getJSONObject(i).getString("image_desc");
                                obj.image_url = jsonArray.getJSONObject(i).getString("image_url");
                                items.add(obj);
                            }
                            adapterImageSlider.setItems(items);
                            viewPager.setAdapter(adapterImageSlider);

                            viewPager.setCurrentItem(0);
                            addBottomDots(layout_dots, adapterImageSlider.getCount(), 0);
                            ((TextView) rootView.findViewById(R.id.title)).setText(items.get(0).image_title);
                            ((TextView) rootView.findViewById(R.id.brief)).setText(items.get(0).image_desc);
                            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                }

                                @Override
                                public void onPageSelected(int position) {
                                    ((TextView) rootView.findViewById(R.id.title)).setText(items.get(position).image_title);
                                    ((TextView) rootView.findViewById(R.id.brief)).setText(items.get(position).image_desc);
                                    addBottomDots(layout_dots, adapterImageSlider.getCount(), position);
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {

                                }
                            });

                            startAutoSlider(adapterImageSlider.getCount());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Image Slider Error: ", error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getContext());
                params.put("access_token",sessionManager.getAccessToken());

                return params;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);

//        initSliderData(rootView);
        return rootView;
    }

//    private void initSliderImage(View rootView) {
//        layout_dots = rootView.findViewById(R.id.layout_dots);
//        viewPager = rootView.findViewById(R.id.view_pager);
//        adapterImageSlider = new AdapterImageSlider(this, new ArrayList<Image>());
//
//        initSliderData(rootView);
//    }

    private void initSliderData(final View rootView) {

    }

    private void startAutoSlider(final int count) {
        runnable = new Runnable() {
            @Override
            public void run() {
                int pos = viewPager.getCurrentItem();
                pos += 1;
                if(pos >= count) pos = 0;
                viewPager.setCurrentItem(pos);
                handler.postDelayed(runnable, 15000);
            }
        };
        handler.postDelayed(runnable, 15000);
    }

    private void addBottomDots(LinearLayout layout_dots, int size, int current) {
        ImageView[] dots = new ImageView[size];

        layout_dots.removeAllViews();
        for (int i=0;i<dots.length;i++){
            dots[i] = new ImageView(getContext());
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10,0,10,0);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle_outline);
            dots[i].setColorFilter(ContextCompat.getColor(getContext(), R.color.grey_40), PorterDuff.Mode.SRC_ATOP);
            layout_dots.addView(dots[i]);
        }

        if(dots.length > 0){
            dots[current].setImageResource(R.drawable.shape_circle);
            dots[current].setColorFilter(ContextCompat.getColor(getContext(), R.color.grey_40), PorterDuff.Mode.SRC_ATOP);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class AdapterImageSlider extends PagerAdapter {

        private Fragment frag;
        private List<Image> items;

//        private AdapterImageSlider.OnItemClickListener onItemClickListener;
//
//        public interface OnItemClickListener {
//            void onItemClick(View view, Image obj);
//        }
//
//        public void setOnItemClickListener(AdapterImageSlider.OnItemClickListener onItemClickListener){
//            this.onItemClickListener = onItemClickListener;
//        }

        //constructor
        public AdapterImageSlider(Fragment fragment, List<Image> items) {
            this.frag = fragment;
            this.items = items;
        }

        @Override
        public int getCount() {
            return this.items.size();
        }

        public Image getItem (int pos){
            return items.get(pos);
        }

        public void setItems(List<Image> items){
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((RelativeLayout) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            final Image o = items.get(position);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.item_slider_image, container, false);

            ImageView imageView = v.findViewById(R.id.image);
//            MaterialRippleLayout lyt_parent = v.findViewById(R.id.lyt_parent);
            Tools.displayImageOriginal(getActivity(), imageView, o.image_url);
//            lyt_parent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(getContext(), "Im clicked", Toast.LENGTH_LONG).show();
//                }
//            });

            ((ViewPager) container).addView(v);
            return v;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ((ViewPager) container).removeView((RelativeLayout) object);
        }
    }

    @Override
    public void onDestroy() {
        if (runnable != null) handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}
