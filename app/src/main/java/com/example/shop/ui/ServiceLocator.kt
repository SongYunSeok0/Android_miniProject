package com.example.shop.ui

import android.content.Context
import com.example.shop.data.ShopDatabase
import com.example.shop.data.UserEntity
import com.example.shop.ui.ShopRepository
import com.example.shop.ui.ShopRetrofit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ServiceLocator {
    lateinit var db: ShopDatabase
        private set

        val repo: ShopRepository by lazy {
            ShopRepository(
                api = ShopRetrofit.api,
                db = db,
                productDao = db.productDao(),
                likeDao = db.likeDao()
            )
        }

        fun init(context: Context) {
            db = ShopDatabase.get(context)

            CoroutineScope(Dispatchers.IO).launch {
            val dao = db.userDao()
            if (dao.countAdmins() == 0) {
                val existing = dao.findByUsername("admin")
                if (existing == null) {
                    dao.insert(
                        UserEntity(
                            username = "admin",
                            password = "admin123",
                            nickname = "관리자",
                            email = "admin@sesac.com",
                            status = "ACTIVE",
                            isAdmin = true
                        )
                    )
                } 
            }
        }
    }
}
