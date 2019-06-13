package com.example.myapplication.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.DiffUtil
import com.example.myapplication.R
import com.example.myapplication.data.model.Article
import com.example.myapplication.data.model.Articles
import me.tatarka.bindingcollectionadapter2.OnItemBind
import me.tatarka.bindingcollectionadapter2.collections.AsyncDiffObservableList
import me.tatarka.bindingcollectionadapter2.recyclerview.BR

sealed class TypeItemView {
    object articleItem : TypeItemView()
    object loadingItem : TypeItemView()
}

class ItemView {
    val asyncDiffObservableList =
        AsyncDiffObservableList<ItemViewModel>(object : DiffUtil.ItemCallback<ItemViewModel>() {
            override fun areItemsTheSame(oldItem: ItemViewModel, newItem: ItemViewModel): Boolean {
                return oldItem.articleData?.title == newItem.articleData?.title
            }

            override fun areContentsTheSame(oldItem: ItemViewModel, newItem: ItemViewModel): Boolean {
                return oldItem.articleData == newItem.articleData
            }
        })

    val item = ObservableArrayList<ItemViewModel>()

    val itemBinding = OnItemBind<ItemViewModel> { itemBinding, position, item ->
        itemBinding.set(
            BR.itemViewModel, when (item.typeItemView) {
                TypeItemView.articleItem -> R.layout.item_row
                else -> R.layout.item_loading_row
            }
        )
    }

    fun buildItemView(articles: Articles?) {
        if (articles != null) {
            item.remove(item.find { it.typeItemView is TypeItemView.loadingItem })
            articles.articles.forEach { article ->
                item.add(ItemViewModel(TypeItemView.articleItem, article))
            }
        } else {
            item.add(ItemViewModel(TypeItemView.loadingItem))
        }
    }

}

class ItemViewModel(
    val typeItemView: TypeItemView,
    val articleData: Article? = null
) {

    val title: String
        get() = articleData?.title ?: "Unknown"

    val desc: String
        get() = articleData?.description ?: "Describtion not shown"

    val imageUrl: String
        get() = articleData?.urlToImage ?: ""

}