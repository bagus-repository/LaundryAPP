package laundry.aslijempolcustomer.activity;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.fragment.HomeFragment;
import laundry.aslijempolcustomer.fragment.ProfileFragment;
import laundry.aslijempolcustomer.fragment.PromoFragment;
import laundry.aslijempolcustomer.fragment.StatusFragment;
import laundry.aslijempolcustomer.fragment.WalletFragment;
import laundry.aslijempolcustomer.utils.BottomNavigationViewHelper;

public class MainActivity extends AppCompatActivity {

    boolean doubleback = false;
    Fragment lastFragment;

    private BottomNavigationView.OnNavigationItemReselectedListener onNavigationItemReselectedListener = new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem item) {

        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                        lastFragment = fragment;
                        return true;
                    case R.id.navigation_status:
                        fragment = new StatusFragment();
                        loadFragment(fragment);
                        lastFragment = fragment;
                        return true;
                    case R.id.navigation_wallet:
                        fragment = new WalletFragment();
                        loadFragment(fragment);
                        lastFragment = fragment;
                        return true;
                    case R.id.navigation_promo:
                        fragment = new PromoFragment();
                        loadFragment(fragment);
                        lastFragment = fragment;
                        return true;
                    case R.id.navigation_profile:
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        lastFragment = fragment;
                        return true;
                }
            return false;
        }
    };

    private void loadFragment(android.support.v4.app.Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.main_navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        Fragment fragment = new HomeFragment();
        loadFragment(fragment);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setOnNavigationItemReselectedListener(onNavigationItemReselectedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(doubleback){
            finish();
        }else{
            this.doubleback = true;
            Toast.makeText(this, "Tekan back sekali lagi untuk keluar aplikasi!", Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleback = false;
            }
        }, 2500);
    }
}
