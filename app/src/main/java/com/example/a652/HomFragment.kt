package com.example.a652

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.a652.databinding.FragmentHomBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentHomBinding
    lateinit var myDB: MyDB

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
        binding = FragmentHomBinding.inflate(inflater,container,false)
        myDB = MyDB(binding.root.context)

        binding.btnLog.setOnClickListener {

           var tel =  binding.telraqamView.text.toString().trim()
            var password = binding.passvordView.text.toString().trim()
            if (tel!=""&&password!=""){

                Log.d("1234", "onCreateView: if")

               var list =  myDB.getallUser()

                Log.d("1234", "onCreateView:list ${list.size}")

                if (list.isNotEmpty()){

                    var user:User?= null

                    for (i in list){

                        if (i.tel_number==tel&&i.password==password){
                          user=i
                        }

                    }

                        if (user!=null){

                    findNavController().navigate(R.id.action_homFragment_to_foydalanuvchiFragment)
                }else Toast.makeText(
                    binding.root.context,
                    "Xato login yoki parol",
                    Toast.LENGTH_SHORT
                ).show()

                }else { Toast.makeText(
                    binding.root.context,
                    "Ro'yxatdan o'tgan foydalanuvchilar mavjud emas",
                    Toast.LENGTH_SHORT
                ).show()
                }

            }else { Toast.makeText(
                binding.root.context,
                "Barcha maydonlarnt to'ldirish shart",
                Toast.LENGTH_SHORT
            ).show()
            }

        }

        binding.btnRegistr.setOnClickListener {
            findNavController().navigate(R.id.action_homFragment_to_regstrFragment)
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}