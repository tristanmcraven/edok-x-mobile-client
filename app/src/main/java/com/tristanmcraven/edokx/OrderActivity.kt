package com.tristanmcraven.edokx

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.tristanmcraven.edok.model.CartItem
import com.tristanmcraven.edok.model.Order
import com.tristanmcraven.edok.model.Restaurant
import com.tristanmcraven.edok.utility.ApiClient
import com.tristanmcraven.edokx.utility.GlobalVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderActivity : AppCompatActivity() {

    private lateinit var textViewOrderNumber: TextView
    private lateinit var textViewRestName: TextView
    private lateinit var textViewRestAddress: TextView
    private lateinit var textViewStatus: TextView
    private lateinit var textViewStatusDescription: TextView
    private lateinit var textViewDeliveryTime: TextView
    private lateinit var MCVCheck: MaterialCardView
    private lateinit var MCVCooking: MaterialCardView
    private lateinit var MCVDelivering: MaterialCardView
    private lateinit var MCVDelivered: MaterialCardView
    private lateinit var buttonGoBack: ImageButton
    private lateinit var buttonOpenCheck: Button
    private lateinit var linearLayoutOrderContents: LinearLayout

    private var orderNumber: UInt = 0u

    private lateinit var order: Order
    private lateinit var rest: Restaurant
    private lateinit var cartItems: List<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        retrieveIntentData()
        initViews()

    }

    fun initViews() {
        textViewOrderNumber = findViewById(R.id.textViewOrderNumber)
        textViewRestName = findViewById(R.id.textViewRestName)
        textViewRestAddress = findViewById(R.id.textViewRestAddress)
        textViewStatus = findViewById(R.id.textViewStatus)
        textViewStatusDescription = findViewById(R.id.textViewStatusDescription)
        textViewDeliveryTime = findViewById(R.id.textViewDeliveryTime)
        MCVCheck = findViewById(R.id.MCVCheck)
        MCVCooking = findViewById(R.id.MCVCooking)
        MCVDelivering = findViewById(R.id.MCVDelivering)
        MCVDelivered = findViewById(R.id.MCVDelivered)
        buttonGoBack = findViewById(R.id.buttonGoBack)
        linearLayoutOrderContents = findViewById(R.id.linearLayoutOrderContents)
        buttonOpenCheck = findViewById(R.id.buttonOpenCheck)
        buttonOpenCheck.setOnClickListener {
            createCheck()
        }

        buttonGoBack.setOnClickListener {
            val intent = Intent(this@OrderActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        CoroutineScope(Dispatchers.IO).launch {
            order = ApiClient.IOrder.getById(orderNumber)!!
            rest = ApiClient.IRestaurant.getById(order.restaurantId)!!
            cartItems = ApiClient.IOrder.getCartItems(order.id)!!

            val views = mutableListOf<View>()

            cartItems.forEach {
                val food = ApiClient.IFood.getById(it.foodId)!!
                views.add(TextView(this@OrderActivity).apply {
                    text = "${it.foodQuantity} x ${food.name} (${it.foodQuantity} x ${food.price}₽)"
                    typeface = ResourcesCompat.getFont(this@OrderActivity, R.font.yandex_sans_light)
                    setTextSize(16f)
                })
            }
            views.add(View(this@OrderActivity).apply {
                setBackgroundColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1
                    ).apply {
                        setMargins(0,25,20,25)
                }
            })

            views.addAll(
                listOf(
                    createTextView("Стоимость корзины: ${order.total - 178u}₽"),
                    createTextView("Стоимость доставки: 109₽"),
                    createTextView("Сервисный сбор: 69₽"),
                    createTextView("Общая сумма заказа: ${order.total}₽")
                )
            )

            withContext(Dispatchers.Main) {
                views.forEach {linearLayoutOrderContents.addView(it)}
                textViewOrderNumber.text = "Заказ №${order.id}"
                textViewRestName.text = "${rest.name}"
                textViewRestAddress.text = rest.address

                when (order.kitchenStatusId) {
                    1u -> {
                        textViewStatus.text = "Принято"
                        textViewStatusDescription.text = "Ресторан начнет готовить заказ, когда найдется свободный курьер"
                        textViewDeliveryTime.text = "Ориентировочное время доставки: ${getEstimatedDeliveryTime(order.createdAt)}"
                        MCVCheck.setCardBackgroundColor(Color.YELLOW)
                    }
                }
            }
        }
    }

    fun retrieveIntentData() {
        orderNumber = intent.getIntExtra("orderNumber", 0).toUInt()
    }

    fun createTextView(text: String) = TextView(this@OrderActivity).apply {
        this.text = text
        typeface = ResourcesCompat.getFont(this@OrderActivity, R.font.yandex_sans_light)
        setTextSize(16f)
    }

    private fun getEstimatedDeliveryTime(createdAt: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val orderTime = LocalDateTime.parse(createdAt, formatter)
        val estimatedTime = orderTime.plusMinutes(45)
        return estimatedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun createCheck() {
        try {
            val time = LocalDateTime.now()

            val byteArrayOutputStream = ByteArrayOutputStream()
            val writer = PdfWriter(byteArrayOutputStream)
            val pdf = PdfDocument(writer)
            val document = Document(pdf)

            val defaultFont = PdfFontFactory.createFont("res/font/yandex_sans_medium.ttf", PdfEncodings.IDENTITY_H)
            val boldFont = PdfFontFactory.createFont("res/font/yandex_sans_bold.ttf", PdfEncodings.IDENTITY_H)

            val topTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f))).useAllAvailableWidth()

            topTable.addCell(Cell().add(Paragraph("кассовый чек / приход").setFont(defaultFont)).setBorder(Border.NO_BORDER))
            topTable.addCell(Cell().add(Paragraph("${time.dayOfMonth}.${time.monthValue}.${time.year} ${time.hour}:${time.minute}").setFont(defaultFont)
                .setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER))

            topTable.addCell(Cell(1,2).add(Paragraph("ОБЩЕСТВО С ОГРАНИЧЕННОЙ\nОТВЕТСТВЕННОСТЬЮ \"ЕДОК\"").setFont(boldFont)
                .setTextAlignment(TextAlignment.CENTER).setMultipliedLeading(0.9f)).setBorder(Border.NO_BORDER))

            topTable.addCell(Cell().add(Paragraph("ИНН").setFont(defaultFont).setMultipliedLeading(0.9f)
                ).setBorder(Border.NO_BORDER).setPadding(0f).setMargin(0f))
            topTable.addCell(Cell().add(Paragraph("9705114405").setFont(defaultFont).setMultipliedLeading(0.9f).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER).setPadding(0f).setMargin(0f))

            topTable.addCell(Cell().add(Paragraph("Налогообложение").setFont(defaultFont)).setBorder(Border.NO_BORDER)
                .setPadding(0f).setMargin(0f))
            topTable.addCell(Cell().add(Paragraph("ОСН").setFont(defaultFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                .setPadding(0f).setMargin(0f))


            val middleTable = Table(UnitValue.createPercentArray(floatArrayOf(2f, 4f, 5f))).useAllAvailableWidth()

            middleTable.addCell(Cell().add(Paragraph("№").setFont(boldFont)).setBorder(Border.NO_BORDER))
            middleTable.addCell(Cell().add(Paragraph("Наименование").setFont(boldFont)).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER))
            middleTable.addCell(Cell().add(Paragraph("Cумма").setFont(boldFont)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER))

            CoroutineScope(Dispatchers.IO).launch {
                var counter = 1
                val items = cartItems.map {
                    val food = ApiClient.IFood.getById(it.foodId)!!
                    Triple(it, food, (food.price.toFloat() / 1.2 * 0.2)) // Precalculate VAT outside UI
                }

                withContext(Dispatchers.Main) {
                    items.forEach { (cartItem, food, vat) ->
                        middleTable.addCell(Cell().add(Paragraph("$counter.").setFont(defaultFont)).setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))
                        middleTable.addCell(Cell().add(Paragraph("${food.name}").setFont(boldFont)).setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))
                        middleTable.addCell(Cell().add(Paragraph("${food.price}₽ x ${cartItem.foodQuantity} = ${food.price * cartItem.foodQuantity}₽").setFont(defaultFont)
                            .setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))

                        middleTable.addCell(Cell().setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))
                        middleTable.addCell(Cell().add(Paragraph("В том числе НДС 20%").setFont(defaultFont)).setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))
                        middleTable.addCell(Cell().add(Paragraph("${String.format("%.2f", vat)}₽")
                            .setFont(defaultFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))

                        middleTable.addCell(Cell().setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))
                        middleTable.addCell(Cell().add(Paragraph("Мера количества").setFont(defaultFont)).setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))
                        middleTable.addCell(Cell().add(Paragraph("шт. или ед.")
                            .setFont(defaultFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))

                        middleTable.addCell(Cell().setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))
                        middleTable.addCell(Cell().add(Paragraph("ИНН поставщика").setFont(defaultFont)).setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))
                        middleTable.addCell(Cell().add(Paragraph("${rest.inn}")
                            .setFont(defaultFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))

                        middleTable.addCell(Cell().setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))
                        middleTable.addCell(Cell().add(Paragraph("Товар / полный расчет").setFont(defaultFont)).setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))
                        middleTable.addCell(Cell().add(Paragraph()
                            .setFont(defaultFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                            .setPadding(0f).setMargin(0f))

                        counter++
                    }
                    val bottomTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f))).useAllAvailableWidth()

                    bottomTable.addCell(Cell().add(Paragraph("Итого").setFont(boldFont)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))
                    bottomTable.addCell(Cell().add(Paragraph("${order.total}₽").setFont(defaultFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))

                    bottomTable.addCell(Cell().add(Paragraph("В том числе НДС 20%").setFont(defaultFont).setFontColor(ColorConstants.GRAY)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))
                    bottomTable.addCell(Cell().add(Paragraph("${String.format("%.2f" , order.total.toFloat() / 1.2 * 0.2)}₽")
                        .setFont(defaultFont).setFontColor(ColorConstants.GRAY).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))

                    bottomTable.addCell(Cell().add(Paragraph("Сумма без НДС").setFont(defaultFont).setFontColor(ColorConstants.GRAY)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))
                    bottomTable.addCell(Cell().add(Paragraph("${String.format("%.2f", order.total.toFloat() - (order.total.toFloat() / 1.2 * 0.2))}₽")
                        .setFont(defaultFont).setFontColor(ColorConstants.GRAY).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))

                    bottomTable.addCell(Cell().add(Paragraph("Безналичными").setFont(boldFont)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))
                    bottomTable.addCell(Cell().add(Paragraph("${order.total}₽").setFont(defaultFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))

                    bottomTable.addCell(Cell().add(Paragraph("Эл. адрес покупателя").setFont(boldFont)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))
                    bottomTable.addCell(Cell().add(Paragraph("${GlobalVM.currentUser!!.email}")
                        .setFont(defaultFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))

                    bottomTable.addCell(Cell().add(Paragraph("Эл. адрес отправителя").setFont(boldFont)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))
                    bottomTable.addCell(Cell().add(Paragraph("no-reply@ofd.edok.ru")
                        .setFont(defaultFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))

                    bottomTable.addCell(Cell().add(Paragraph("Адрес сайта ФНС").setFont(boldFont)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))
                    bottomTable.addCell(Cell().add(Paragraph("www.nalog.gov.ru")
                        .setFont(defaultFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))

                    bottomTable.addCell(Cell().add(Paragraph("Название ОФД").setFont(boldFont)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))
                    bottomTable.addCell(Cell().add(Paragraph("Едок.ОФД")
                        .setFont(defaultFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))

                    bottomTable.addCell(Cell().add(Paragraph("ИНН ОФД").setFont(boldFont)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))
                    bottomTable.addCell(Cell().add(Paragraph("7704358518")
                        .setFont(defaultFont).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER)
                        .setPadding(0f).setMargin(0f))

                    document.add(topTable)
                    document.add(middleTable)
                    document.add(bottomTable)
                    document.close()

                    openPdfFromByteArray(byteArrayOutputStream.toByteArray())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun openPdfFromByteArray(pdf: ByteArray) {
        val file = File(this@OrderActivity.cacheDir, "temp.pdf")
        FileOutputStream(file).use {
            it.write(pdf)
        }

        val uri = FileProvider.getUriForFile(this@OrderActivity, "${packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        this@OrderActivity.startActivity(intent)
    }
}