Рассмотрим Вкратце что делает `demo.c`:

1. Создаем тредпул с 5-ю тредами
2. Добавляем 50 задач: вывести номер соответствующей задачи.
3. Удаляем тредпул


Для начала проанализируем с помощью helgrind.


Обнаружена гонка данных между 1-м и 3-м потоком. 3-й поток пишет в адрес 0x4A7E058 (это pool->tpool_head) на 31 строчке tpool.c, а на 10 строчке 1-й поток читает оттуда. Этого можно избежать, используя атомики c CAS, сделав join или взяв мьютекст перед тем, как прочитать оттуда.
```
int is_taskover(tpool_t *pool) {
    pthread_mutex_lock(&pool->queue_lock);
    int result = (pool->tpool_head == NULL);
    pthread_mutex_unlock(&pool->queue_lock);
    return result;
}
```

Аналогично для 1-го и 2-го потока. 1-й поток пишет в адрес 0x4A7E040 (это pool->shutdown) на 120 строчке tpool.c, а на 20 строчке 2-й поток читает оттуда. Здесь можно загнать pool->shutdown уже под имеющийся лок.
```
   pthread_mutex_lock(&pool->queue_lock);
   pool->shutdown = 1;
   pthread_cond_broadcast(&pool->queue_ready);
   pthread_mutex_unlock(&pool->queue_lock);
 
```

Аналогичные две ошибки выдает ThreadSanitizer.