package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.interceptors.GzipRequestInterceptor
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.example.myapplication.data.model.Articles
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.viewmodel.ItemView
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import com.jakewharton.rxbinding3.recyclerview.scrollStateChanges
import com.jakewharton.rxbinding3.view.attaches
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.draws
import com.jakewharton.rxbinding3.view.focusChanges
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    companion object {
        const val BASE_URL = "https://newsapi.org/v2/everything"
        const val KEYWORD = "q"
        const val API_KEY = "apiKey"
        const val PAGE_SIZE = "pageSize"
        const val PAGE = "page"
        const val DEFAULT_PAGE_SIZE = 10
    }

    lateinit var binding : ActivityMainBinding

    val compositeDisposable = CompositeDisposable()

    var currentPage = 1
    val interceptor = with(HttpLoggingInterceptor()){
        level = HttpLoggingInterceptor.Level.BODY
        return@with this
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.itemView = ItemView()
        initRecycler("apple")
    }

    private fun initRecycler(keyword: String) {


        val rvObservable = rv_news
            .focusChanges()
            .switchMap {
                requestNews(keyword, currentPage)
            }
            .switchMap {
                rv_news.scrollStateChanges()
            }
            .filter { it == RecyclerView.SCROLL_STATE_IDLE }
            .subscribeOn(AndroidSchedulers.mainThread())
            .filter {
                (rv_news.layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition() == (rv_news.adapter?.itemCount ?: 0) -1
            }
            .observeOn(AndroidSchedulers.mainThread())
            .map { currentPage++ }
            .switchMap {
                requestNews(keyword, it)
            }
            .subscribe({Log.e("succes","true")}, {Log.e("failed", it.message)})

        compositeDisposable.add(
            rvObservable
        )
    }

    private fun requestNews(keyword: String, page: Int) = Rx2AndroidNetworking.get(BASE_URL)
        .addQueryParameter(KEYWORD, keyword)
        .addQueryParameter(API_KEY, BuildConfig.API_KEY)
        .addQueryParameter(PAGE_SIZE, DEFAULT_PAGE_SIZE.toString())
        .addQueryParameter(PAGE, page.toString())
        .setOkHttpClient(okHttpClient)
        .build()
        .getObjectObservable(Articles::class.java)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe {
            binding.itemView?.buildItemView(null)
        }
        .doAfterNext {
            binding.itemView?.buildItemView(it)
        }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
