package com.payu.otp_assist_sample

import android.os.Bundle
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.payu.otpassist.PayUOtpAssist
import com.payu.otpassist.PayUOtpAssistConfig
import com.payu.otpassist.listeners.PayUOtpAssistCallback
import com.payu.otpassist.models.PayUAcsRequest
import com.payu.paymentparamhelper.PaymentPostParams
import com.payu.paymentparamhelper.PayuConstants
import java.security.MessageDigest


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

/**
 * A simple [Fragment] subclass.
 * Use the [NewCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewCardFragment : Fragment(), TextWatcher {
    var etCard: EditText? = null
    var etCVV: EditText? = null
    var etExpiry: EditText? = null
    var btnPay: Button? = null
    var switchSaveCard: SwitchCompat? = null
    var checkBox: CheckBox? = null
    var bin: String? = null
    private var key: String? = null
    private var email: String? = null
    private var salt: String? = null
    private var tobeSaved: Boolean = false
    private var mChangeWasAddition = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            key = it.getString(ARG_PARAM1)
            email = it.getString(ARG_PARAM2)
            salt = it.getString(ARG_PARAM3)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etCard = view.findViewById(R.id.et_add_card)
        etCVV = view.findViewById(R.id.etCvv)
        etExpiry = view.findViewById(R.id.etExpiry)
        btnPay = view.findViewById(R.id.btnPay)
        switchSaveCard = view.findViewById(R.id.switchSaveCard)
        checkBox = view.findViewById(R.id.checkBox)
        etCard?.addTextChangedListener(this)
        btnPay?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                createPaymentParams()
            }
        })
        checkBox?.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                tobeSaved = p1
            }
        })
        etExpiry?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mChangeWasAddition = p3 > p2
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                if (mChangeWasAddition) {
                    if (editable?.length == 1 && Character.getNumericValue(editable[0]) >= 2) {
                        prependLeadingZero(editable)
                    }
                }
                val paddingSpans = editable?.getSpans(0, editable?.length, SlashSpan::class.java)
                for (span in paddingSpans!!) {
                    editable.removeSpan(span)
                }
                addDateSlash(editable)

            }
        })
    }

    private fun addDateSlash(editable: Editable) {
        val index = 2
        val length = editable.length
        if (index <= length) {
            editable.setSpan(
                SlashSpan(), index - 1, index,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun prependLeadingZero(editable: Editable) {
        val firstChar = editable[0]
        editable.replace(0, 1, "0").append(firstChar)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewCardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String?, param2: String?, param3: String?) =
            NewCardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                }
            }
    }

    override fun afterTextChanged(p0: Editable?) {
        if (p0?.length!! == 6) {
            bin = p0?.toString()
            val binsList = ArrayList<String>()
            binsList.add(p0.toString())
            val hashString =
                key + "|oneClickEligibility|" + convertArrayToCSV(binsList) + "|".plus(salt)
            val hash = calculateHash(hashString)
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    private fun convertArrayToCSV(bins: ArrayList<String>): String {
        var result: String? = ""
        for (value in bins) {
            result = result.plus(value)
            result = result.plus(',')
        }
        return result?.substring(0, result.length.minus(1))!!
    }

    fun createPaymentParams() {
        val paymentParams = com.payu.paymentparamhelper.PaymentParams()
        paymentParams.key = key
        paymentParams.amount = "1"
        paymentParams.productInfo = "Prod Info"
        paymentParams.firstName =
            "First Name"
        paymentParams.email = "mobile@payu.in"
        paymentParams.surl = "https://payu.herokuapp.com/success"
        paymentParams.userCredentials = key.plus(":").plus(email)
        paymentParams.storeCard = 1
//        paymentParams.ipurl = "https://payu.herokuapp.com/success"
//        paymentParams.termUrl = "https://payu.herokuapp.com/success"
        paymentParams.furl = "https://payu.herokuapp.com/failure"
        paymentParams.udf1 = "udf1"
        paymentParams.udf2 = "udf2"
        paymentParams.udf3 = "udf3"
        paymentParams.udf4 = "udf4"
        paymentParams.udf5 = "udf5"
        paymentParams.txnId = System.currentTimeMillis().toString()
        Log.v("PAYU","TxnId ==============> "+paymentParams.txnId)
//        paymentParams.txnId = "9def1355-5e94-47a8-a6e9-8d54f7b1e065"
        paymentParams.notifyURL = paymentParams.surl
//        paymentParams.cardNumber = "4166431000004613"
        paymentParams.cardNumber = etCard?.text.toString()
        paymentParams.cardName = "PayU"
        paymentParams.phone = "9999999999"
        paymentParams.bankCode = "CC"
        paymentParams.pg = "CC"
        paymentParams.expiryMonth = etExpiry?.text.toString().substring(0, 2)
         paymentParams.expiryYear =
                "20"+etExpiry?.text.toString()
                        .substring(2)
        paymentParams.cvv = etCVV?.text.toString()
        paymentParams.storeCard = if (tobeSaved) 1 else 0
        paymentParams.storeCard=1
        val paymentHashData =
            paymentParams.key + "|" + paymentParams.txnId + "|" + paymentParams.amount + "|" + paymentParams.productInfo + "|" + paymentParams.firstName + "|" + paymentParams.email + "|" + paymentParams.udf1 + "|" +
                    paymentParams.udf2 + "|" + paymentParams.udf3 + "|" + paymentParams.udf4 + "|" + paymentParams.udf5 + "||||||" + salt

        paymentParams.hash = calculateHash(paymentHashData)
        var postData = PaymentPostParams(paymentParams, PayuConstants.CC).paymentPostParams
        if (postData.status.equals("ERROR")) {
            Toast.makeText(requireContext(), postData.result, Toast.LENGTH_SHORT).show()
        } else {
            Log.v("OneClick", "Payment Postdata ..... $postData")
            makePaymentViaOTPAssist(postData.result)
        }


    }

    private fun makePaymentViaOTPAssist(paymentPostData: String) {
        var otpAssistCallback = object : PayUOtpAssistCallback() {
            override fun onError(errorCode: String?, errorMessage: String?) {
                if (null!=activity&& !activity?.isFinishing!! && !activity?.isDestroyed!!)
                Toast.makeText(activity, "onError: $errorMessage", Toast.LENGTH_SHORT).show()
            }

            override fun onPaymentFailure(merchantResponse: String?, payUResponse: String?) {
                if (null!=activity&& !activity?.isFinishing!! && !activity?.isDestroyed!!)
                Toast.makeText(activity, "onPaymentFailure: $payUResponse", Toast.LENGTH_SHORT).show()
            }

            override fun onPaymentSuccess(merchantResponse: String?, payUResponse: String?) {
                if (null!=activity&& !activity?.isFinishing!! && !activity?.isDestroyed!!)
                Toast.makeText(activity, "onPaymentSuccess: $payUResponse", Toast.LENGTH_SHORT).show()

            }
//override shouldHandleFallback if you want to handle fallback(Bank page) scenario.

        }
        val payUOtpAssistConfig = PayUOtpAssistConfig()
        payUOtpAssistConfig.shouldAllowAutoSubmit = true
        payUOtpAssistConfig.postData = paymentPostData

        PayUOtpAssist.open(
            requireActivity(),
            otpAssistCallback, payUOtpAssistConfig
        )
    }

    //TODO: Never calculate hash here, it must be calculated from the backend.
    private fun calculateHash(hashString: String): String? {
        Log.v("payu", hashString)
        val messageDigest =
            MessageDigest.getInstance("SHA-512")
        messageDigest.update(hashString.toByteArray())
        val mdbytes = messageDigest.digest()
        return getHexString(mdbytes)
    }

    private fun getHexString(data: ByteArray): String {
        // Create Hex String
        val hexString: StringBuilder = StringBuilder()
        for (aMessageDigest: Byte in data) {
            var h: String = Integer.toHexString(0xFF and aMessageDigest.toInt())
            while (h.length < 2)
                h = "0$h"
            hexString.append(h)
        }
        return hexString.toString()
    }
}