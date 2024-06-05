package com.example.useraddreessbdbootcamp.views.addresslist

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.useraddreessbdbootcamp.R
import com.example.useraddreessbdbootcamp.database.AppDataBase
import com.example.useraddreessbdbootcamp.repository.MainRepository
import com.example.useraddreessbdbootcamp.viewmodels.address.AddressViewModel
import com.example.useraddreessbdbootcamp.viewmodels.user.MyViewModelFactory


class AddressActivity : AppCompatActivity() {
    private lateinit var addressAdapter: AddressListAdapter
    private lateinit var addressViewModel: AddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        val userId = intent.getLongExtra("USER_ID", -1)

        if (userId == -1L) {
            // Manejar caso en el que no se proporciona un ID de usuario válido
            finish()
            return
        }

        val recyclerView = findViewById<RecyclerView>(R.id.direccionestxt)
        addressAdapter = AddressListAdapter { /* Acción al hacer clic en una dirección */ }
        recyclerView.adapter = addressAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar MyViewModelFactory
        val repository = MainRepository(AppDataBase.getDatabase(application).userDao(), AppDataBase.getDatabase(application).addressDao())
        val factory = MyViewModelFactory(application, repository)

        // Inicializar el ViewModel
        addressViewModel = ViewModelProvider(this, factory).get(AddressViewModel::class.java)

        // Observar las direcciones del usuario por ID
        addressViewModel.getAddressForUser(userId)
        addressViewModel.usersLV.observe(this) { addresses ->
            addresses?.let {
                addressAdapter.submitList(it)
            }
        }
    }
}