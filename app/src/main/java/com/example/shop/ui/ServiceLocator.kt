package com.example.shop.ui

import android.content.Context
import com.example.shop.data.ShopDatabase
import com.example.shop.data.UserEntity
import com.example.shop.data.UserDao
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
                // 이미 'admin'이 있으면 그 계정을 승격, 없으면 새로 생성
                val existing = dao.findByUsername("admin")
                if (existing == null) {
                    dao.insert(
                        UserEntity(
                            username = "admin",
                            password = "admin123",  // 데모용. 실제론 해시 권장
                            status = "ACTIVE",
                            isAdmin = true
                        )
                    )
                } 
            }
        }
    }
}
