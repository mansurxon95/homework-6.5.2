package com.example.a652

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.a652.databinding.DialogLayoutBinding
import com.example.a652.databinding.DialogSmsBinding
import com.example.a652.databinding.FragmentFoydalanuvchiBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FoydalanuvchiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FoydalanuvchiFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentFoydalanuvchiBinding
    lateinit var myDB: MyDB
    lateinit var adapter:Rc_View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoydalanuvchiBinding.inflate(inflater,container,false)

        myDB = MyDB(binding.root.context)

        val getallUser = myDB.getallUser()
        adapter = Rc_View(object :Rc_View.OnClik{


            override fun view(contact: User, position: Int) {
                super.view(contact, position)

                var alertDialog = BottomSheetDialog(binding.root.context)
                var dialogLayout = DialogLayoutBinding.inflate(LayoutInflater.from(binding.root.context),null,false)
                dialogLayout.itemName.text = contact.lastFirst_name
                dialogLayout.itemDavlatManzil.text = "${contact.davlat}, ${contact.viloyat}"
                dialogLayout.itemImage.setImageURI(contact.image)
                alertDialog.setContentView(dialogLayout.root)
                alertDialog.show()

                dialogLayout.btnCall.setOnClickListener {
                    val intent = Intent(Intent.ACTION_CALL);
                    intent.data = Uri.parse("tel:${contact.tel_number}")
                    startActivity(intent)
                    alertDialog.dismiss()
                }

                dialogLayout.btnSms.setOnClickListener {
                    alertDialog.dismiss()
                    var alertDialog2 = BottomSheetDialog(binding.root.context)
                    var dialogLayout2 = DialogSmsBinding.inflate(LayoutInflater.from(binding.root.context),null,false)

                    dialogLayout2.telNumber.setText("${contact.tel_number}")
                    alertDialog2.setContentView(dialogLayout2.root)
                    alertDialog2.show()

                    dialogLayout2.btnSend.setOnClickListener {
                        var num = dialogLayout2.telNumber.text
                        var text = dialogLayout2.textSms.text

                        try {

                            // on below line we are initializing sms manager.
                            //as after android 10 the getDefault function no longer works
                            //so we have to check that if our android version is greater
                            //than or equal toandroid version 6.0 i.e SDK 23
                            val smsManager: SmsManager
                            if (Build.VERSION.SDK_INT>=23) {
                                //if SDK is greater that or equal to 23 then
                                //this is how we will initialize the SmsManager
                                smsManager = binding.root.context.getSystemService(SmsManager::class.java)
                            }
                            else{
                                //if user's SDK is less than 23 then
                                //SmsManager will be initialized like this
                                smsManager = SmsManager.getDefault()
                            }

                            // on below line we are sending text message.
                            smsManager.sendTextMessage(num.toString(), null, text.toString(), null, null)

                            // on below line we are displaying a toast message for message send,
                            Toast.makeText(binding.root.context, "Habar yuborildi", Toast.LENGTH_LONG).show()
                           alertDialog2.dismiss()
                        } catch (e: Exception) {

                            // on catch block we are displaying toast message for error.
                            Toast.makeText(binding.root.context, "Iltimos, barcha ma'lumotlarni kiriting.."+e.message.toString(), Toast.LENGTH_LONG)
                                .show()
                        }

                    }

                }


            }

            override fun edit(contact: User, position: Int, view: View) {
                super.edit(contact, position, view)
//                registerForContextMenu(view)
            }

        })
        binding.rcView.adapter = adapter

        adapter.submitList(getallUser)






        return binding.root
    }

//    override fun onCreateContextMenu(
//        menu: ContextMenu,
//        v: View,
//        menuInfo: ContextMenu.ContextMenuInfo?
//    ) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//        var menuInflater = MenuInflater(binding.root.context)
//        menuInflater.inflate(R.menu.menu_contex,menu)
//    }
//
//    override fun onContextItemSelected(item: MenuItem): Boolean {
//        return super.onContextItemSelected(item)
//
//        val itemId = item.itemId
//
//
//
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FoydalanuvchiFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FoydalanuvchiFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}