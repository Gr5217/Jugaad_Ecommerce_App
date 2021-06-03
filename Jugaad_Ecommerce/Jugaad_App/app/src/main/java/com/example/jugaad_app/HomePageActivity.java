package com.example.jugaad_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jugaad_app.Admin.AdminMaintainProductsActivity;
import com.example.jugaad_app.Model.Products;
import com.example.jugaad_app.Prevalent.Prevalent;
import com.example.jugaad_app.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomePageActivity extends AppCompatActivity {

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton logout;
    private FloatingActionButton settings;
    private FloatingActionButton fab;
    private FloatingActionButton search;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page2);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            type = getIntent().getExtras().get("Admin").toString();
        }

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        logout = (FloatingActionButton) findViewById(R.id.Logout);
        settings = (FloatingActionButton) findViewById(R.id.Settings);
        fab = (FloatingActionButton) findViewById(R.id.Cart);
        search = (FloatingActionButton) findViewById(R.id.Search);
        Paper.init(this);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!type.equals("Admin")) {
                    Paper.book().destroy();

                    Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!type.equals("Admin")) {
                    Intent intent = new Intent(HomePageActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!type.equals("Admin")) {
                    Intent intent = new Intent(HomePageActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!type.equals("Admin")) {
                    Intent intent = new Intent(HomePageActivity.this, SearchProductsActivity.class);
                    startActivity(intent);
                }
            }
        });

        TextView userNameTextView = (TextView) findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = (CircleImageView) findViewById(R.id.user_profile_image);

        if (!type.equals("Admin")){
            userNameTextView.setText(Prevalent.currentOnlineUser.getName());
            Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
        }

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>().setQuery(ProductsRef,Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products) {
                productViewHolder.txtproductName.setText(products.getProd_name());
                productViewHolder.txtproductDescription.setText(products.getDescription());
                productViewHolder.txtproductPrice.setText("Price = Rs." + products.getPrice());
                Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (type.equals("Admin")){
                            Intent intent = new Intent(HomePageActivity.this, AdminMaintainProductsActivity.class);
                            intent.putExtra("pid",products.getPid());
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(HomePageActivity.this,ProductDetailsActivity.class);
                            intent.putExtra("pid",products.getPid());
                            startActivity(intent);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}