package com.example.useraddreessbdbootcamp.views.listusers

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.useraddreessbdbootcamp.R
import com.example.useraddreessbdbootcamp.database.AppDataBase
import com.example.useraddreessbdbootcamp.entities.Address
import com.example.useraddreessbdbootcamp.entities.User
import com.example.useraddreessbdbootcamp.repository.MainRepository
import com.example.useraddreessbdbootcamp.viewmodels.address.AddressViewModel
import com.example.useraddreessbdbootcamp.viewmodels.user.MyViewModelFactory
import com.example.useraddreessbdbootcamp.viewmodels.user.UserViewModel
import com.example.useraddreessbdbootcamp.viewmodels.user.UserViewModelFactory
import com.example.useraddreessbdbootcamp.views.addresslist.AddressActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewModelUser: UserViewModel
    private var userId: Long = -1L
    private val factory: MyViewModelFactory by lazy { MyViewModelFactory(application, MainRepository(AppDataBase.getDatabase(application).userDao(), AppDataBase.getDatabase(application).addressDao())) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = AppDataBase.getDatabase(application)
        val repository = MainRepository(database.userDao(), database.addressDao())
        val factory = MyViewModelFactory(application,repository)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = UserListAdapter{ user -> onUserClick(user) }

        viewModelUser = ViewModelProvider(this,factory)[UserViewModel::class.java]
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModelUser.usersLV.observe(this){ user ->
            user?.let { adapter.submitList(it) }
        }

        val addUserButton: Button = findViewById(R.id.btn_add_user)

        addUserButton.setOnClickListener {
            showAlertDialogInsertUser()
        }


       // val user = User( userName = "Nombre Ejemplo")

    }

    private fun onUserClick(user: User) {
        val optionsMenu = arrayOf("Ver Direcciones", "Actualizar Usuario", "Eliminar Usuario", "Ingresar Dirección")
        AlertDialog.Builder(this)
            .setTitle("Selecciona una opción")
            .setItems(optionsMenu) { dialog, selected ->
                when (selected) {
                    0 -> viewAddresses(user)
                    1 -> showUpdateUser(user)
                    2 -> showDeleteUser(user)
                    3 -> addAddressForUser(user)
                }
            }
            .show()
    }

    private fun showDeleteUser(user: User) {

        AlertDialog.Builder(this)
            .setTitle("Borrar Usuario")
            .setMessage("¿ Esta seguro que desea borrar este usuario?")
            .setPositiveButton("Sí"){dialog, _ ->
                viewModelUser.deleteUser(user)
                dialog.dismiss()
            }
            .setNegativeButton("No"){dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun showAlertDialogInsertUser(){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar un Usuario")

        val inputUserName = EditText(this)
        inputUserName.inputType = InputType.TYPE_CLASS_TEXT

        builder.setView(inputUserName)
        builder.setPositiveButton("OK"){ dialog, _ ->
            val userName = inputUserName.text.toString()
            if( userName.isNotEmpty() ){
                viewModelUser.insertUser(User(userName = userName)){ userId ->
                    if( userId != -1L){
                        Toast.makeText(this, "Usuario Agregado con ID ${ userId }", Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(this, "Error al insertar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar"){ dialog , _-> dialog.cancel() }
        builder.show()

    }

    private fun showUpdateUser(user: User) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar un Usuario")
        val inputUserName = EditText(this)
        inputUserName.inputType = InputType.TYPE_CLASS_TEXT

        inputUserName.setText(user.userName)
        builder.setView(inputUserName)

        builder.setPositiveButton("OK"){ dialog, _ ->
            val username = inputUserName.text.toString()
            if( username.isNotEmpty() ){
                user.userName = username
                viewModelUser.updateUser(user)
                /**
                 * Se agrega el viewModelUser.getAllUser() para que muestre
                 * nuevamente la lista de usuarios actualizadas con el update
                 */
                viewModelUser.getAllUser()
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog , _-> dialog.cancel() }
        builder.show()

    }


    /**
     * Para crear la direccion del usuario
     */

    private fun addAddressForUser(user: User) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Dirección")

        val inputStreet = EditText(this)
        inputStreet.hint = "Calle"
        val inputCity = EditText(this)
        inputCity.hint = "Ciudad"
        val inputNumber = EditText(this)
        inputNumber.hint = "Número"

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(inputStreet)
        layout.addView(inputCity)
        layout.addView(inputNumber)
        builder.setView(layout)

        builder.setPositiveButton("OK") { dialog, _ ->
            val street = inputStreet.text.toString()
            val city = inputCity.text.toString()
            val number = inputNumber.text.toString().toIntOrNull()

            if (street.isNotEmpty() && city.isNotEmpty() && number != null) {
                val address = Address(userOwnerId = user.userId, street = street, city = city, number = number)


                val addressViewModel = ViewModelProvider(this, factory)[AddressViewModel::class.java]


                addressViewModel.insertAddress(address, user.userId)
                Toast.makeText(this,"Se ingreso la direccion Correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun viewAddresses(user: User){
        val intent = Intent(this, AddressActivity::class.java)
        intent.putExtra("USER_ID", user.userId)
        startActivity(intent)
    }
}