package com.payu.otp_assist_sample

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.payu.otpassist.PayUOtpAssist
import com.payu.otpassist.PayUOtpAssistConfig
import com.payu.otpassist.listeners.PayUOtpAssistCallback
import com.payu.otpassist.network.PayUAsyncTaskResponse
import com.payu.otpassist.network.PayUNetworkData
import com.payu.otpassist.network.PayUNetworkHandler
import com.payu.paymentparamhelper.PaymentPostParams
import com.payu.paymentparamhelper.PayuConstants
import org.json.JSONObject
import java.security.MessageDigest


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "key"
private const val ARG_PARAM2 = "email"
private const val ARG_PARAM3 = "salt"
var NAME_ON_CARD = "name_on_card"
var CARD_NAME = "card_name"
var EXPIRY_YEAR = "expiry_year"
var EXPIRY_MONTY = "expiry_month"
var CARD_TYPE = "card_type"
var CARD_TOKENS = "card_tokens" // we have it already

var CARD_TOKEN = "card_token" // we have it already

var IS_EXPIRED = "is_expired"
var CARD_MODE = "card_mode"
var CARD_NO = "card_no"
var CARD_BRAND = "card_brand"
var CARD_BIN = "card_bin"
var IS_DOMESTIC = "isDomestic"
var ISSUINGBANK = "issuingBank"
var CARDTYPE = "cardType"
var CARDCATEGORY = "cardCategory"

/**
 * A simple [Fragment] subclass.
 * Use the [SaveCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SaveCardFragment : Fragment(), View.OnClickListener,
    CardSelected {
    override fun onCardSelected(storedCard: StoredCard?) {
        this.storedCard = storedCard
    }

    override fun onCVVEntered(cvv: String) {
        this.cvv = cvv
    }

    // TODO: Rename and change types of parameters
    private var key: String? = null
    private var email: String? = null
    private var salt: String? = null
    var recyclerView: RecyclerView? = null
    var button: Button? = null
    val userCardArrayList: ArrayList<StoredCard?> = ArrayList<StoredCard?>()
    var storeCardAdapter: SaveCardAdapter? = null
    var storedCard: StoredCard? = null
    private var cvv: String? = null
    private var contentProgress: ContentLoadingProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            key = it.getString(ARG_PARAM1)
            email = it.getString(ARG_PARAM2)
            salt = it.getString(ARG_PARAM3)
        }
        getStoredCards()
    }

    fun getStoredCards() {
        val var1 = key.plus(':').plus(email)
//        val hashString = "$key|get_user_cards|$key:$email|$salt"
        val hashString = "$key|get_user_cards|$var1|$salt"
        val hash = calculateHash(hashString)
        val payUNetworkData = PayUNetworkData()
        payUNetworkData.type = "save"
        payUNetworkData.url = "https://info.payu.in/merchant/postservice.php?form=2"
//        payUNetworkData.request = "key=$key&command=get_user_cards&var1=$key:$email&hash=$hash"
        payUNetworkData.request = "key=$key&command=get_user_cards&var1=$var1&hash=$hash"
        PayUNetworkHandler().executeApi(payUNetworkData, object : PayUAsyncTaskResponse {
            override fun onPayUAsyncTaskResponse(
                requestType: String,
                response: String,
                headers: okhttp3.Headers?,
                responseCode:Int
            ) {
                contentProgress?.hide()
                Log.v("payu", response)
                val jsonObject = JSONObject(response)

                if (jsonObject.has("user_cards")) { // yey! we have stored cards..
                    val cardsList: JSONObject = jsonObject.getJSONObject("user_cards")
                    val keysIterator: Iterator<String> = cardsList.keys()
                    while (keysIterator.hasNext()) {
                        val card: JSONObject = cardsList.getJSONObject(keysIterator.next())
                        var userCard: StoredCard? =
                            StoredCard(
                                card.getString(
                                    PayuConstants.NAME_ON_CARD
                                )
                            )
                        userCard?.expiryYear = card.getString(EXPIRY_YEAR)
                        userCard?.expiryMonth = card.getString(EXPIRY_MONTY)
                        userCard?.cardName = card.getString(CARD_NAME)
                        userCard?.cardType = (card.getString(CARD_TYPE))
                        userCard?.cardToken = (card.getString(CARD_TOKEN))
                        userCard?.isExpired = (if (card.getInt(IS_EXPIRED) === 0) false else true)
                        userCard?.cardMode = (card.getString(CARD_MODE))
                        userCard?.maskedCardNumber = (card.getString(CARD_NO))
                        userCard?.cardBrand = (card.getString(CARD_BRAND))
                        userCard?.cardBin = (card.getString(CARD_BIN))
                        userCard?.isDomestic = (card.getString(IS_DOMESTIC))
                        userCardArrayList.add(userCard)
                        userCard = null
                    }
                }
                if (null == storeCardAdapter) {
                    storeCardAdapter =
                        SaveCardAdapter(
                            userCardArrayList,
                            this@SaveCardFragment
                        )
                }
                recyclerView?.layoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                recyclerView?.adapter = storeCardAdapter
            }

        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_save_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rvCards)
        button = view.findViewById<Button>(R.id.button)
        contentProgress = view.findViewById(R.id.progress_circular)
        contentProgress?.show()
        button?.setOnClickListener(this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SaveCardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String?, param2: String?, param3: String?) =
            SaveCardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                }
            }
    }

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

    override fun onClick(p0: View?) {
        createPaymentParams()
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
        paymentParams.userCredentials =
            key.plus(":").plus(email)
//        paymentParams.ipurl = "https://payu.herokuapp.com/success"
//        paymentParams.termUrl = "https://payu.herokuapp.com/success"
        paymentParams.furl = "https://payu.herokuapp.com/failure"
        paymentParams.udf1 = "udf1"
        paymentParams.udf2 = "udf2"
        paymentParams.udf3 = "udf3"
        paymentParams.udf4 = "udf4"
        paymentParams.udf5 = "udf5"
        paymentParams.txnId = System.currentTimeMillis().toString()
//        paymentParams.txnId = "9def1355-5e94-47a8-a6e9-8d54f7b1e065"
        paymentParams.notifyURL = paymentParams.surl
//        paymentParams.cardNumber = this.storedCard?.maskedCardNumber
        paymentParams.cardToken = this.storedCard?.cardToken
        paymentParams.cardName = this.storedCard?.cardName
        paymentParams.phone = "8630874608"
        paymentParams.bankCode = "CC"
        paymentParams.pg = "CC"
        paymentParams.expiryMonth = this.storedCard?.expiryMonth
        paymentParams.expiryYear = this.storedCard?.expiryYear
        paymentParams.cvv = this.cvv
        val paymentHashData =
            paymentParams.key + "|" + paymentParams.txnId + "|" + paymentParams.amount + "|" + paymentParams.productInfo + "|" + paymentParams.firstName + "|" + paymentParams.email + "|" + paymentParams.udf1 + "|" +
                    paymentParams.udf2 + "|" + paymentParams.udf3 + "|" + paymentParams.udf4 + "|" + paymentParams.udf5 + "||||||" + salt

        paymentParams.hash = calculateHash(paymentHashData)
        val postData = PaymentPostParams(paymentParams, PayuConstants.CC).paymentPostParams.result
        Log.v("OneClick", "Payment Postdata ..... $postData")
        makePaymentViaOTPAssist(postData)
    }

    private fun makePaymentViaOTPAssist(paymentPostData: String) {
        var otpAssistCallback = object : PayUOtpAssistCallback(){
            override fun onError(errorCode: String?, errorMessage: String?) {
                if (null!=activity&& !activity?.isFinishing!! && !activity?.isDestroyed!!)
                Toast.makeText(requireActivity(), "onError: $errorMessage", Toast.LENGTH_SHORT).show()
            }

            override fun onPaymentFailure(merchantResponse: String?, payUResponse: String?) {
                if (null!=activity&& !activity?.isFinishing!! && !activity?.isDestroyed!!)
                Toast.makeText(activity, "onPaymentFailure: $payUResponse", Toast.LENGTH_SHORT).show()
            }

            override fun onPaymentSuccess(merchantResponse: String?, payUResponse: String?) {
                if (null!=activity&& !activity?.isFinishing!! && !activity?.isDestroyed!!)
                Toast.makeText(activity, "onPaymentSuccess: $payUResponse", Toast.LENGTH_SHORT).show()

            }

        }
        val payUOtpAssistConfig = PayUOtpAssistConfig()
        payUOtpAssistConfig.shouldAllowAutoSubmit = true
        payUOtpAssistConfig.postData = paymentPostData
        payUOtpAssistConfig.merchantLogo = requireActivity().getDrawable(R.drawable.payu_otp_pay_u_logo)

        PayUOtpAssist.open(
            this.context!!,
            otpAssistCallback, payUOtpAssistConfig
        )
    }
    fun toggleKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            0
        )
    }
}