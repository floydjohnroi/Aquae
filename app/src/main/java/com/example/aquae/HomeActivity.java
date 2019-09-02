package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.util.Objects;

import static android.widget.Toast.makeText;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    MaterialCardView toolbarCard;
    FloatingActionButton floatingActionButton;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView userName, userEmail, toolbarTitle;
    MaterialSearchView searchView;

    DatabaseReference databaseReference;
    Session session;

    boolean flag;
    boolean doubleBackToExitPressedOnce = false;

    Toast toast;

    public static final String CHANNEL_ID = "AQUAE";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_home);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        session = new Session(getApplicationContext());

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TEST", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        Log.d("TEST", token);
                        Toast.makeText(HomeActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
        userEmail = navigationView.getHeaderView(0).findViewById(R.id.userEmail);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));

        toolbarTitle.setOnClickListener(v -> searchView.showSearch());

        searchViewCode();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerView.bringToFront();
                drawerView.requestLayout();
                searchView.closeSearch();
            }
        };


        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        floatingActionButton.setOnClickListener(v -> {
            if (flag) {
                getSupportFragmentManager().beginTransaction().replace(R.id.drawerContent, new FilterFragment()).commit();
                floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_map));
                flag = false;
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.drawerContent, new MapFragment()).commit();
                floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_list));
                flag = true;
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.drawerContent, new FilterFragment()).commit();
            floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_map));
            flag = false;
        }

        String name = capitalize(session.getFirstname()) + " " + capitalize(session.getLastname());
        userName.setText(name);
        userEmail.setText(session.getUsername());

        toast = Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT);

    }

    private void searchViewCode() {
        searchView = findViewById(R.id.search_view);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                makeText(getApplicationContext(),query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//
//        MenuItem item = menu.findItem(R.id.cart);
//        item.setIcon(R.drawable.icon_cart_dark);
//        MenuItemCompat.setActionView(item, R.layout.cart_badge);
//        RelativeLayout cart = (RelativeLayout) MenuItemCompat.getActionView(item);
//        ImageView cartIcon = cart.findViewById(R.id.cartIcon);
//        final TextView badge = cart.findViewById(R.id.badge);
//        cartIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_cart_dark));
//
//        cartIcon.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CartActivity.class)));
//
//        databaseReference.child("carts").orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
//                .addValueEventListener(new ValueEventListener() {
//                    @TargetApi(Build.VERSION_CODES.KITKAT)
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        int c = 0;
//
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            for (DataSnapshot data : snapshot.getChildren()) {
//                                for (DataSnapshot d : data.getChildren()) {
//                                    for (DataSnapshot e : d.getChildren()) {
//                                        for (DataSnapshot f : e.getChildren()) {
//                                            try {
//                                                c += Integer.parseInt(String.valueOf(f.child("quantity").getValue()));
//                                            } catch (NumberFormatException nfe) {
//                                                nfe.printStackTrace();
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        if (c < 1) {
//                            badge.setVisibility(View.GONE);
//                        }
//                        else if (c > 9) {
//                            badge.setVisibility(View.VISIBLE);
//                            badge.setText("9+");
//                        }
//                        else {
//                            badge.setVisibility(View.VISIBLE);
//                            badge.setText(String.valueOf(c));
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        switch (item.getItemId()) {
            case R.id.search_view:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;

            toast.show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toast.cancel();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
//            case R.id.cart:
//                startActivity(new Intent(HomeActivity.this, CartActivity.class));
//                break;
            case R.id.wallet:
                startActivity(new Intent(HomeActivity.this, WalletActivity.class));
                break;
            case R.id.logout:
                session.clearSession();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
                break;
        }

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();

        return true;
    }

    private String capitalize(final String line) {

        String[] strings = line.split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : strings) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            stringBuilder.append(cap).append(" ");
        }

        return stringBuilder.toString();
    }

}
