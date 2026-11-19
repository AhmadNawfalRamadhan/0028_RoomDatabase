package com.example.activity7.repositori

import com.example.activity7.room.Siswa
import com.example.activity7.room.SiswaDao
import kotlinx.coroutines.flow.Flow

interface RepositoriSiswa {
    fun getAllSiswaStream(): Flow<List<Siswa>>
    suspend fun insertSiswa(siswa: Siswa)

    fun getSiswaStream(id: Int): Flow<Siswa?>

}

class OfflineRepositoriSiswa(
    private val siswaDao: SiswaDao
): RepositoriSiswa {
    override fun getAllSiswaStream(): Flow<List<Siswa>> = siswaDao
        .getAllSiswa()
    override suspend fun insertSiswa(siswa: Siswa) = siswaDao.insert(siswa)

    override fun getSiswaStream(id: Int): Flow<Siswa?> = siswaDao.getsiswa(id)
    override suspend fun  deleteSiswa(siswa: Siswa) = siswaDao.delete(siswa)
    override suspend fun  updateSiswa(siswa: Siswa) = siswaDao.update(siswa)
}