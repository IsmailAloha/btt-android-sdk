package com.bluetriangle.analytics.caching.provider

import com.bluetriangle.analytics.caching.classifier.CacheType

interface PayloadProvider {

    fun getByType(cacheType: CacheType): List<PayloadComponent>

    fun getExpired():List<PayloadComponent>

    fun removeById(id: String, type: CacheType)

}