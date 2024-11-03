package vn.edu.usth.outlook.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import vn.edu.usth.outlook.Email_Sender;
import vn.edu.usth.outlook.KeyboardVisibilityUtils;
import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.fragment.AppContactFragment;
import vn.edu.usth.outlook.fragment.ArchiveFragment;
import vn.edu.usth.outlook.fragment.CalendarFragment;
import vn.edu.usth.outlook.fragment.ContactFragment;
import vn.edu.usth.outlook.fragment.DeletedFragment;
import vn.edu.usth.outlook.fragment.DraftFragment;
import vn.edu.usth.outlook.fragment.JunkFragment;
import vn.edu.usth.outlook.fragment.NotificationFragment;
import vn.edu.usth.outlook.fragment.SentFragment;
import vn.edu.usth.outlook.fragment.SettingsFragment;
import vn.edu.usth.outlook.fragment.UnwantedFragment;
import vn.edu.usth.outlook.listener.SelectListener;
import vn.edu.usth.outlook.adapter.CustomAdapter;
import vn.edu.usth.outlook.fragment.InboxFragment;

public class MainActivity extends AppCompatActivity implements SelectListener, KeyboardVisibilityUtils.OnKeyboardVisibilityListener {

    RecyclerView recyclerView;
    //    List<Email> List;
    public static List<Email_Sender> emailList = new ArrayList<>();
    CustomAdapter customAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ExtendedFloatingActionButton compose_button;
    CoordinatorLayout coordinatorLayout;
    BottomAppBar bottomAppBar;
    BottomNavigationView bottomNavigationView;
    KeyboardVisibilityUtils keyboardVisibilityUtils;
    SearchView searchView;
    private Fragment currentFragment;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar background to the corresponding
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background_all));

        setContentView(R.layout.activity_main);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        compose_button = findViewById(R.id.Compose);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        ImageView searchIcon = findViewById(R.id.search_icon);
        ImageButton notificationIcon = findViewById(R.id.notification_icon);
        searchView = findViewById(R.id.search_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ggicon);
        // Lấy NavigationView và header view
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        // Lấy nút đóng từ header view
        ImageButton closeBtn = headerView.findViewById(R.id.btn_close_nav);
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new NotificationFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.drawer_layout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                findViewById(R.id.recycler_main).setVisibility(View.GONE);


            }
        });
        searchIcon.bringToFront();
        // Set onClickListener for the search icon to toggle SearchView visibility
        searchIcon.setOnClickListener(v -> {
            if (searchView.getVisibility() == View.GONE) {
                searchView.setVisibility(View.VISIBLE);
                searchView.requestFocus();
                notificationIcon.setVisibility(View.GONE);
            } else {
                searchView.setVisibility(View.GONE);
                searchView.clearFocus();
                notificationIcon.setVisibility(View.VISIBLE);
            }
        });
        searchIcon.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Trigger the click behavior manually
                v.performClick();
                return true;
            }
            return false;
        });
        displayItems();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng Navigation Drawer khi nhấn nút
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Thay đổi biểu tượng khi mở Drawer
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ggicon);
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        // Đặt biểu tượng mong muốn ngay từ đầu (khi khởi động)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ggicon);

        // Set a click listener for the navigation button in the toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the navigation drawer on the start (left) side is open
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });


        compose_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ComposeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Side-bar navigation
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);

                if (item.getItemId() == R.id.inbox) {
                    recyclerView.setVisibility(View.VISIBLE);
                    notificationIcon.setVisibility(View.VISIBLE);
                    openFragment(new InboxFragment(),"Inbox");
                    return true;
                } else if (item.getItemId() == R.id.draft) {
                    recyclerView.setVisibility(View.GONE);
                    searchIcon.setVisibility(View.VISIBLE);
                    notificationIcon.setVisibility(View.GONE);
                    openFragment(new DraftFragment(),"Draft");
                    return true;
                } else if (item.getItemId() == R.id.settings) {
                    recyclerView.setVisibility(View.GONE);
                    notificationIcon.setVisibility(View.GONE);
                    searchIcon.setVisibility(View.INVISIBLE);
                    compose_button.setVisibility(View.GONE);
                    openFragment(new SettingsFragment(),"Settings");
                    return true;
                } else if (item.getItemId() == R.id.sent) {
                    recyclerView.setVisibility(View.GONE);
                    searchIcon.setVisibility(View.VISIBLE);
                    notificationIcon.setVisibility(View.GONE);
                    openFragment(new SentFragment(),"Sent");
                    return true;
                } else if (item.getItemId() == R.id.archive) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    searchIcon.setVisibility(View.VISIBLE);
                    notificationIcon.setVisibility(View.GONE);
                    openFragment(new ArchiveFragment(),"Archive");
                    return true;
                } else if (item.getItemId() == R.id.junk) {
                    recyclerView.setVisibility(View.GONE);
                    notificationIcon.setVisibility(View.GONE);
                    openFragment(new JunkFragment(),"Junk");
                    return true;
                } else if (item.getItemId() == R.id.sent) {
                    recyclerView.setVisibility(View.GONE);
                    searchIcon.setVisibility(View.VISIBLE);
                    notificationIcon.setVisibility(View.GONE);
                    openFragment(new SentFragment(),"Sent");
                    return true;
                } else if (item.getItemId() == R.id.deleted) {
                    recyclerView.setVisibility(View.GONE);
                    searchIcon.setVisibility(View.VISIBLE);
                    notificationIcon.setVisibility(View.GONE);
                    openFragment(new DeletedFragment(),"Deleted");
                    return true;
                } else if (item.getItemId() == R.id.unwanted) {
                    recyclerView.setVisibility(View.GONE);
                    searchIcon.setVisibility(View.VISIBLE);
                    notificationIcon.setVisibility(View.GONE);
                    openFragment(new UnwantedFragment(),"Unwanted");
                    return true;
                }
                return false;
            }
        });

        // Bottom bar navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    recyclerView.setVisibility(View.VISIBLE);
                    notificationIcon.setVisibility(View.VISIBLE);
                    searchView.setQueryHint(getString(R.string.search_in_mail));
                    compose_button.setText(R.string.New_mail);
                    compose_button.setIconResource(R.drawable.ic_compose);
                    compose_button.setVisibility(View.VISIBLE);
                    openFragment(new InboxFragment(),"Inbox");
                    return true;
                } else if (itemId == R.id.contact) {
                    recyclerView.setVisibility(View.GONE);
                    notificationIcon.setVisibility(View.GONE);
                    searchIcon.setVisibility(View.GONE);
                    compose_button.setIconResource(R.drawable.plus_compose);
                    compose_button.setText(R.string.new_contact);
                    compose_button.setVisibility(View.VISIBLE);
                    openFragment(new ContactFragment(),"Contact");
                    return true;
                } else if (itemId == R.id.calendar) {
                    recyclerView.setVisibility(View.GONE);
                    notificationIcon.setVisibility(View.GONE);
                    searchIcon.setVisibility(View.VISIBLE);
                    compose_button.setIconResource(R.drawable.plus_compose);
                    compose_button.setVisibility(View.VISIBLE);
                    compose_button.setText(R.string.new_contact);
                    openFragment(new CalendarFragment(),"Calendar");
                    return true;
                } else if (itemId == R.id.app_contact) {
                    currentFragment = new AppContactFragment();
                    recyclerView.setVisibility(View.GONE);
                    searchIcon.setVisibility(View.GONE);
                    notificationIcon.setVisibility(View.GONE);
                    compose_button.setVisibility(View.GONE);
                    openFragment(new AppContactFragment(),"App Contact");
                    return true;
                }

                return false;
            }
        });


        // Add a scroll listener to the RecyclerView
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && compose_button.isExtended()) {
                    // Scrolling down, and FAB is extended, so shrink it
                    compose_button.shrink();
                    bottomAppBar.setVisibility(View.GONE);

                } else if (dy < 0 && !compose_button.isExtended()) {
                    // Scrolling up, and FAB is not extended, so extend it
                    compose_button.extend();
                    bottomAppBar.setVisibility(View.VISIBLE);

                }
            }
        });

    }

    //  Search bar
    private void filter(String newText) {
        List<Email_Sender> filteredList = new ArrayList<>();
        for (Email_Sender item : emailList) {
            if (item.getSender().toLowerCase().startsWith(newText.toLowerCase())) {
                filteredList.add(item);
            }
        }
        customAdapter.filterList(filteredList);
    }


    @Override
    public void onKeyboardVisibilityChanged(boolean isVisible) {
        if (isVisible) {
            // Keyboard is open, hide the BottomAppBar and FAB
            bottomAppBar.setVisibility(View.GONE);
            compose_button.setVisibility(View.GONE);
        } else {
            // Keyboard is closed, show the BottomAppBar and FAB
            bottomAppBar.setVisibility(View.VISIBLE);
                compose_button.setVisibility(View.VISIBLE);
            }


    }
    // Display items in RecyclerView
    private void displayItems() {
        recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));  // Single column grid (like list)

        // Initialize email list
        emailList = new ArrayList<>();
        emailList.add(new Email_Sender("user1@example.com", "Do sth", "Subject 1", "me"));
        emailList.add(new Email_Sender("user2@example.com", "Make sth", "Subject 2", "Long"));
        emailList.add(new Email_Sender("user3@example.com", "Play sth", "Subject 3", "me"));



    // Create adapter and set it to the RecyclerView
    customAdapter = new CustomAdapter(this, emailList,this);
    recyclerView.setAdapter(customAdapter);

    //Add swipe-to-delete or other functionalities with ItemTouchHelper
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
    itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    //Swipe to do delete and archive
    Email_Sender deletedMail = null;
    List<String> archivedMail = new ArrayList<>();
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    // Handle left swipe (delete)
                    deletedMail = emailList.get(position); // Get the deleted email
                    emailList.remove(position); // Remove it from the list
                    customAdapter.notifyItemRemoved(position); // Notify the adapter

                    // Show a Snackbar with an undo option
                    Snackbar.make(recyclerView, "Email deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // User clicked "Undo," so add the deleted email back to the list
                                    if (deletedMail != null) {
                                        emailList.add(position, deletedMail);
                                        customAdapter.notifyItemInserted(position);
                                    }
                                }
                            }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    final Email_Sender email = emailList.get(position);

                    archivedMail.add(String.valueOf(email));
                    emailList.remove(position);
                    customAdapter.notifyItemRemoved(position);

                    Snackbar.make(recyclerView,"Conversation Archived.", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    archivedMail.remove(email);
                                    emailList.add(position, email);
                                    customAdapter.notifyItemInserted(position);
                                }
                            }).show();
                    break;
            }

        }
    };

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(MainActivity.this, DetailMail.class);
        intent.putExtra("Name", emailList.get(position).getSender());
        intent.putExtra("Head Mail", emailList.get(position).getSubject());
        intent.putExtra("Me", emailList.get(position).getReceiver());
        intent.putExtra("Content", emailList.get(position).getContent());
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(int position) {

    }
    private void openFragment(Fragment fragment,String title) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
        // Set the toolbar title dynamically based on the fragment being opened
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}


