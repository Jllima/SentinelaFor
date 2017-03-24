package br.sentinelafor.jorge.sentinelafor.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import br.sentinelafor.jorge.sentinelafor.R;
import br.sentinelafor.jorge.sentinelafor.adapter.MoviesAdapter;
import br.sentinelafor.jorge.sentinelafor.model.Movie;
import br.sentinelafor.jorge.sentinelafor.model.MoviesResponse;
import br.sentinelafor.jorge.sentinelafor.rest.ApiClient;
import br.sentinelafor.jorge.sentinelafor.rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MoviesActivity.class.getSimpleName();

    private final static String API_KEY = "c3e54e77357768b0423792d6a346d192";

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        if(API_KEY.isEmpty()){
            Toast.makeText(getApplicationContext(),"Não possui chave",Toast.LENGTH_LONG).show();
            return;
        }

        recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchMovies();
            }
        });
    }

    @Override
    public void onRefresh() {
        fetchMovies();
    }

    private void fetchMovies(){
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<MoviesResponse> call = apiService.getTopRatedMovies(API_KEY);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                List<Movie> movies = response.body().getResults();
                recyclerView.setAdapter(new MoviesAdapter(movies,R.layout.list_item_movie,getApplicationContext()));
                Log.d(TAG, "Número de filmes recebidos: "+movies.size());
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}
