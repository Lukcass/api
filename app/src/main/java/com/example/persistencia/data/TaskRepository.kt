package com.example.persistencia.data

import com.example.persistencia.remote.MockApi
import kotlinx.coroutines.flow.Flow

/**
 * Offline-first por usuario: Room es la fuente de verdad y la nube se sincroniza aparte.
 * sync() = subir cambios locales (push) y luego traer lo de la nube (pull).
 */
class TaskRepository(
    private val dao: TaskDao,
    private val username: String
) {

    val allTasks: Flow<List<Task>> = dao.observeByUser(username)

    suspend fun getTaskById(id: Int): Task? = dao.getTaskById(id)

    suspend fun insert(task: Task): Long =
        dao.insertTask(task.copy(username = username, isSynced = false))

    /** Relee la fila actual para no pisar el remoteId si la sincronización lo asignó entre tanto. */
    suspend fun update(id: Int, transform: (Task) -> Task) {
        val current = dao.getTaskById(id) ?: return
        dao.updateTask(transform(current).copy(isSynced = false))
    }

    suspend fun delete(id: Int) {
        val current = dao.getTaskById(id) ?: return
        if (current.remoteId == null) {
            dao.deleteTask(current) // nunca se subió: basta con borrarla local
        } else {
            // se oculta de la UI y se borra en la nube en el próximo sync
            dao.updateTask(current.copy(pendingDelete = true, isSynced = false))
        }
    }

    suspend fun adoptLocalTasks() = dao.adoptOrphans(username)

    suspend fun sync() {
        push()
        pull()
    }

    private suspend fun push() {
        for (t in dao.getUnsyncedByUser(username)) {
            if (t.pendingDelete) {
                t.remoteId?.let { MockApi.deleteTask(it) }
                dao.deleteTask(t)
                continue
            }
            val rid = t.remoteId
            val newRemoteId = when {
                rid == null -> MockApi.createTask(t)
                MockApi.updateTask(rid, t) -> rid
                else -> MockApi.createTask(t) // alguien la borró en la nube: se recrea
            }
            val actual = dao.getTaskById(t.id)
            if (actual == null) {
                MockApi.deleteTask(newRemoteId) // se borró local mientras se subía
                continue
            }
            // Solo queda "sincronizada" si no cambió durante la subida
            dao.updateTask(actual.copy(remoteId = newRemoteId, isSynced = actual == t))
        }
    }

    private suspend fun pull() {
        val remote = MockApi.fetchTasks(username)
        val remoteIds = remote.map { it.remoteId }.toSet()
        val locals = dao.getAllByUser(username)
        val byRemoteId = locals.mapNotNull { l -> l.remoteId?.let { it to l } }.toMap()

        for (r in remote) {
            val l = byRemoteId[r.remoteId]
            if (l == null) {
                dao.insertTask(
                    Task(
                        titulo = r.titulo,
                        descripcion = r.descripcion,
                        estadoCompletado = r.estadoCompletado,
                        fechaCreacion = r.fechaCreacion,
                        isSynced = true,
                        username = username,
                        remoteId = r.remoteId
                    )
                )
            } else {
                val cur = dao.getTaskById(l.id) ?: continue
                val changed = cur.titulo != r.titulo || cur.descripcion != r.descripcion ||
                        cur.estadoCompletado != r.estadoCompletado || cur.fechaCreacion != r.fechaCreacion
                // Si hay cambios locales pendientes, ganan los locales (se subirán en el próximo ciclo)
                if (cur.isSynced && changed) {
                    dao.updateTask(
                        cur.copy(
                            titulo = r.titulo,
                            descripcion = r.descripcion,
                            estadoCompletado = r.estadoCompletado,
                            fechaCreacion = r.fechaCreacion
                        )
                    )
                }
            }
        }

        // Tareas sincronizadas que ya no existen en la nube (borradas desde otro dispositivo)
        for (l in locals) {
            val rid = l.remoteId ?: continue
            if (rid !in remoteIds) {
                val cur = dao.getTaskById(l.id)
                if (cur != null && cur.isSynced) dao.deleteTask(cur)
            }
        }
    }
}