package com.rstech.inventory.di

import com.rstech.inventory.data.repository.AuthRepositoryImpl
import com.rstech.inventory.data.repository.NoteRepositoryImpl // <--- 1. Importa la implementación
import com.rstech.inventory.data.repository.ProductRepositoryImpl
import com.rstech.inventory.data.repository.PurchaseRepositoryImpl
import com.rstech.inventory.data.repository.SaleRepositoryImpl
import com.rstech.inventory.data.repository.UserRepositoryImpl
import com.rstech.inventory.domain.repository.AuthRepository
import com.rstech.inventory.domain.repository.NoteRepository // <--- 2. Importa la interfaz del dominio
import com.rstech.inventory.domain.repository.ProductRepository
import com.rstech.inventory.domain.repository.PurchaseRepository
import com.rstech.inventory.domain.repository.SaleRepository
import com.rstech.inventory.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindPurchaseRepository(
        purchaseRepositoryImpl: PurchaseRepositoryImpl
    ): PurchaseRepository

    // 3. Añade este binding para las notas
    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        noteRepositoryImpl: NoteRepositoryImpl
    ): NoteRepository

    @Binds
    @Singleton
    abstract fun bindSaleRepository(
        saleRepositoryImpl: SaleRepositoryImpl
    ): SaleRepository
}