package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.access.pro.config.ConfigModel
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.application.BaseActivity
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.SubVipInterface
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ActivitySubVipBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.AdapterSub_1
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.AdapterSub_2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SubVipActivity : BaseActivity(), SubVipInterface.View {
    private lateinit var binding: ActivitySubVipBinding
    private var restore = false
    private var i = 0
    private val handler = Handler()
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            val currentItem: Int = binding.viewPager2.getCurrentItem()
            val count: Int = 4
            if (currentItem < count - 1) {
                binding.viewPager2.setCurrentItem(currentItem + 1, true)
            } else {
                binding.viewPager2.setCurrentItem(0, true)
            }
            handler.postDelayed(this, 3000) // 3000 milliseconds (3 seconds) delay between scrolls
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubVipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val list1 = listOf(
            Pair("LOL With Realistic Fart Sound", R.drawable.item_sv1_1),
            Pair("Meme Sound For Pranks", R.drawable.item_sv1_2),
            Pair("Troll With Funny Sounds", R.drawable.item_sv1_3),
            Pair("Air Horn Stir The Atmosphere", R.drawable.item_sv1_4)
        )
        binding.viewPager.adapter = AdapterSub_1(list1)
        val list2 = listOf(
            Triple(
                "Jennie Mark",
                "I can't believe, how fun and surprised the Prank Sound is! I'm blown away!",
                R.drawable.item_sv2_1
            ),
            Triple(
                "Lisa Lee",
                "Wowww!! The Meme Sounds on the app make me laugh so much!",
                R.drawable.item_sv2_2
            ),
            Triple(
                "Bam Bam",
                "I was surprised by the list of Fart sounds! They are very lively!",
                R.drawable.item_sv2_3
            ),
            Triple(
                "Mark JK",
                "Oh my ghost! The Haircut sounds are so realistic. It make my girlfriend cry!",
                R.drawable.item_sv1_4
            )
        )
        binding.viewPager2.adapter = AdapterSub_2(list2)
        binding.circle.setViewPager(binding.viewPager2)

        with(binding)
        {
            setupBilling()
             when (ConfigModel.subDefaultPack) {
                "pack_sub_week" -> {
                    currentProduct = subWeekProduct
                    handelClick(2)
                }

                "pack_sub_month" -> {
                    currentProduct = subMonthProduct
                    handelClick(1)

                }

                "pack_life_time" -> {
                    currentProduct = lifeTimeProduct
                    handelClick(3)

                }
            }
            btnStart.setOnClickListener {
                showPurchaseDialog()
            }
            close.setOnClickListener {
                if (intent.getStringExtra("activity") == "setting") {
                    finish()
                } else {
                    finish()

                }
            }
            restore.setOnClickListener {
                this@SubVipActivity.restore = true
                billingClient!!.startConnection(billingClientStateListener)
            }
            mLinearMonth.setOnClickListener {
                handelClick(1)
                showPurchaseDialog()
            }

            mLinearWeek.setOnClickListener {
                handelClick(2)
                showPurchaseDialog()
            }

            mLinearLifeTime.setOnClickListener {
                handelClick(3)
                showPurchaseDialog()
            }
            txtPolicy.setOnClickListener {
                val intent = Intent(this@SubVipActivity, ShowWebActitvity::class.java)
                intent.putExtra(
                    "link",
                    "https://sites.google.com/view/privacypolicytosforhaircutpran/"
                )
                startActivity(intent)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable);

    }
    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 3000) // 3000 milliseconds (3 seconds) initial delay
    }

    override fun setBackGroundDefault(draw: Int) {
        with(binding) {
            val drawable = getDrawable(draw)
            mLinearMonth.background = drawable
            mLinearWeek.background = drawable
            mLinearLifeTime.background = drawable
        }
    }

    override fun setTextColorDefault(color: Int) {
        with(binding) {
            val color = getColor(color)
            txtWeek.setTextColor(color)
            txtMonth.setTextColor(color)
            txtLifeTime.setTextColor(color)
        }
    }

    override fun setBackGroundClick(draw: Int, position: Int) {
        with(binding) {
            val drawable = getDrawable(draw)
            when (position) {
                1 -> mLinearMonth.background = drawable
                2 -> mLinearWeek.background = drawable
                3 -> mLinearLifeTime.background = drawable
            }
        }
    }

    override fun setTextColorClick(color: Int, position: Int) {
        with(binding) {
            val color = getColor(color)
            when (position) {
                1 -> txtMonth.setTextColor(color)
                2 -> txtWeek.setTextColor(color)
                3 -> txtLifeTime.setTextColor(color)
            }
        }
    }

    override fun handelClick(position: Int) {
        setBackGroundDefault(R.drawable.boder_buy_unclick)
        setBackGroundClick(R.drawable.border_buy_click, position)
        setTextColorDefault(R.color.clickSub)
        setTextColorClick(R.color.white, position)
        when (position) {
            1 -> currentProduct = subMonthProduct
            2 -> currentProduct = subWeekProduct
            3 -> currentProduct = lifeTimeProduct
        }
    }


    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        proApplication.isSubVip = true
                        //update UI hiển thị đã mua
                        if (!purchase.isAcknowledged) {
                            val acknowledgePurchaseParams =
                                AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.purchaseToken).build()

                            billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                            }
                        }
                    }
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            } else {
            }
        }

    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                lifecycleScope.launch {
                    processPurchases()
                    getActivePurchase()
                    runOnUiThread {
                        if (proApplication.isSubVip) {
                            //update UI hien thi da mua vip
                        } else {
                            //update UI hien thi chua mua
                        }

                        if (restore) {
                            Toast.makeText(
                                this@SubVipActivity,
                                "Restore Success",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this@SubVipActivity,
                    billingResult.debugMessage,
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

        override fun onBillingServiceDisconnected() {
            Toast.makeText(
                this@SubVipActivity,
                "Connect billing service failed! Check Your network connection.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private var subWeekProduct: ProductDetails? = null
    private var subMonthProduct: ProductDetails? = null
    private var lifeTimeProduct: ProductDetails? = null
    private var currentProduct: ProductDetails? = null


    private fun nextActivity() {

    }

    private fun setupBilling() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient!!.startConnection(billingClientStateListener)
    }

    suspend fun processPurchases() {
        subMonthProduct =
            getPurchasesProductDetail("pack_sub_month", BillingClient.ProductType.SUBS)
        subWeekProduct = getPurchasesProductDetail("pack_sub_week", BillingClient.ProductType.SUBS)
        lifeTimeProduct =
            getPurchasesProductDetail("pack_life_time", BillingClient.ProductType.INAPP)
        currentProduct = subWeekProduct
        when (ConfigModel.subDefaultPack) {
            "pack_sub_week" -> {
                currentProduct = subWeekProduct
            }

            "pack_sub_month" -> {
                currentProduct = subMonthProduct

            }

            "pack_life_time" -> {
                currentProduct = lifeTimeProduct

            }
        }


        //  runOnUiThread {
        fillDataToUI()
        // }
    }

    private fun fillDataToUI() {
        if (subMonthProduct != null && !subMonthProduct!!.subscriptionOfferDetails.isNullOrEmpty()) {
            var product = subMonthProduct!!.subscriptionOfferDetails!!.first()
            var pricingPhase = product.pricingPhases.pricingPhaseList
            if (pricingPhase.size > 1) {
                binding.txtPriceMonth.text = "Then\n".plus(
                    product.pricingPhases.pricingPhaseList[1].formattedPrice
                ).plus("/month")
                binding.txtMonth.text =
                    product.pricingPhases.pricingPhaseList[0].formattedPrice.plus("\nFor 3 Day")
            } else if (pricingPhase.isNotEmpty()) {
                binding.txtPriceMonth.text =
                    "Then\n".plus(
                        product.pricingPhases.pricingPhaseList[0].formattedPrice
                    ).plus("/month")
                binding.txtMonth.text =
                    product.pricingPhases.pricingPhaseList[0].formattedPrice
            }
        }
        if (subWeekProduct != null && !subWeekProduct!!.subscriptionOfferDetails.isNullOrEmpty()) {
            var product = subWeekProduct!!.subscriptionOfferDetails!!.first()
            var pricingPhase = product.pricingPhases.pricingPhaseList
            if (pricingPhase.size > 1) {
                binding.txtPriceWeek.text = "Then\n".plus(
                    product.pricingPhases.pricingPhaseList[1].formattedPrice
                ).plus("/week")
                binding.txtWeek.text =
                    product.pricingPhases.pricingPhaseList[0].formattedPrice.plus("\nFor 3 Day")
            } else if (pricingPhase.isNotEmpty()) {
                binding.txtPriceWeek.text = "Then\n".plus(
                    product.pricingPhases.pricingPhaseList[0].formattedPrice
                ).plus("/week")
                binding.txtWeek.text =
                    product.pricingPhases.pricingPhaseList[0].formattedPrice
            }
        }
        if (lifeTimeProduct != null) {
            val product = lifeTimeProduct!!.oneTimePurchaseOfferDetails
            if (product != null) {
                binding.txtPriceLifeTime.text = product.formattedPrice
            }
        }
        binding.description.text =
            getString(R.string.billing_starts)
                .plus(" ")
                .plus(binding.txtWeek.text.toString().replace("Then ", ""))
                .plus("/week or ")
                .plus(binding.txtPriceMonth.text.toString().replace("Then ", ""))
                .plus("/month or ")
                .plus(binding.txtPriceLifeTime.text.toString().replace("Then ", ""))
                .plus(" for LifeTime. ")
                .plus(getString(R.string.billing_end))
        Log.d("ádasdsdd", subMonthProduct.toString() + "" + subWeekProduct + lifeTimeProduct)
    }

    private suspend fun getPurchasesProductDetail(
        packId: String,
        productType: String
    ): ProductDetails? {
        val productList = mutableListOf<QueryProductDetailsParams.Product>()
        productList.add(
            QueryProductDetailsParams.Product.newBuilder().setProductId(packId)
                .setProductType(productType).build()
        )
        val paramsSub = QueryProductDetailsParams.newBuilder()
        paramsSub.setProductList(productList)
        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient!!.queryProductDetails(paramsSub.build())
        }
        if (!productDetailsResult.productDetailsList.isNullOrEmpty()) {
            return productDetailsResult.productDetailsList!!.first()
        } else {
            return null
        }
    }


    private fun onClick() {
        // logic click hien thi subdialog

//        binding.btnClose.setOnClickListener {
//            nextActivity()
//        }
//        binding.btnContinue.setOnClickListener {
//            if (proApplication.isSubVip) {
//                nextActivity()
//            } else {
//                showPurchaseDialog()
//            }
//        }
//        binding.btnSubMonth.setOnClickListener {
//            currentProduct = subMonthProduct
//            showPurchaseDialog()
//        }
//        binding.btnSubWeek.setOnClickListener {
//            currentProduct = subWeekProduct
//            showPurchaseDialog()
//        }
//        binding.btnLifeTime.setOnClickListener {
//            currentProduct = lifeTimeProduct
//            showPurchaseDialog()
//        }
//
//        binding.btnReStore.setOnClickListener {
//            restore = true
//            billingClient!!.startConnection(billingClientStateListener)
//        }
    }

    private fun showPurchaseDialog() {
        if (currentProduct != null) {
            val productDetailsParamsList: List<BillingFlowParams.ProductDetailsParams>
            if (currentProduct!!.productType == BillingClient.ProductType.SUBS) {
                productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(currentProduct!!)
                        .setOfferToken(currentProduct!!.subscriptionOfferDetails!!.first().offerToken)
                        .build()
                )
            } else {
                productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(currentProduct!!)
                        .build()
                )
            }
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            billingClient!!.launchBillingFlow(this@SubVipActivity, billingFlowParams)
        } else {
            restore = false
            billingClient!!.startConnection(billingClientStateListener)
        }
    }


}