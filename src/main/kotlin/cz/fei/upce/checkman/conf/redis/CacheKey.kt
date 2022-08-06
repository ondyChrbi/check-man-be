package cz.fei.upce.checkman.conf.redis

interface CacheKey {
    fun toCacheKey() : String
    fun toAllCacheKeyPattern(): String
}
