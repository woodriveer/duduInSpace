package br.com.woodriver.domain

class ObjectPool<T>(
    private val maxSize: Int,
    private val factory: () -> T,
    private val reset: (T) -> Unit
) {
    private val pool = mutableListOf<T>()
    private val activeObjects = mutableSetOf<T>()

    fun obtain(): T {
        val obj = if (pool.isEmpty()) {
            factory()
        } else {
            pool.removeAt(pool.size - 1)
        }
        activeObjects.add(obj)
        return obj
    }

    fun free(obj: T) {
        if (activeObjects.remove(obj) && pool.size < maxSize) {
            reset(obj)
            pool.add(obj)
        }
    }

    fun freeAll() {
        activeObjects.forEach { obj ->
            if (pool.size < maxSize) {
                reset(obj)
                pool.add(obj)
            }
        }
        activeObjects.clear()
    }

    fun getActiveCount(): Int = activeObjects.size
} 