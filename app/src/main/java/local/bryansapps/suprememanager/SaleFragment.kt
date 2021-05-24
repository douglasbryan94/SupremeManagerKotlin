package local.bryansapps.suprememanager

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout

class SaleFragment(private val saleActivity: SaleActivity,
                   private val location: LocationsEnum,
                   private val levels: Map<ProductsEnum, Int>) : Fragment(), View.OnClickListener {
    private var txtCreamSale: EditText? = null
    private var txtMilkSale: EditText? = null
    private var swtchSoapSale: SwitchCompat? = null
    private var txtCupSale: EditText? = null
    private var swtchStirStickSale: SwitchCompat? = null
    private var swtchCoffeeSale: SwitchCompat? = null
    private var swtchHotChocolateSale: SwitchCompat? = null
    private var swtchEqualSale: SwitchCompat? = null
    private var swtchSugarSale: SwitchCompat? = null
    private var swtchGreenTeaSale: SwitchCompat? = null
    private var swtchOrangePekoeSale: SwitchCompat? = null
    private var swtchFilterSale: SwitchCompat? = null
    private var btnCommit: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sale_fragment, container, false)

        txtCreamSale = view.findViewById(R.id.txtCreamValue)
        txtMilkSale = view.findViewById(R.id.txtMilkValue)
        swtchSoapSale = view.findViewById(R.id.swtchSoap)
        txtCupSale = view.findViewById(R.id.txtCupsValue)
        swtchStirStickSale = view.findViewById(R.id.swtchStirSticks)
        swtchCoffeeSale = view.findViewById(R.id.swtchCoffee)
        swtchHotChocolateSale = view.findViewById(R.id.swtchHotChocolate)
        swtchEqualSale = view.findViewById(R.id.swtchEqual)
        swtchSugarSale = view.findViewById(R.id.swtchSugar)
        swtchGreenTeaSale = view.findViewById(R.id.swtchGreenTea)
        swtchOrangePekoeSale = view.findViewById(R.id.swtchOrangePekoe)
        swtchFilterSale = view.findViewById(R.id.swtchFilters)

        btnCommit = view.findViewById(R.id.btnCommit)
        btnCommit!!.setOnClickListener(this)

        return view
    }

    override fun onStart() {
        super.onStart()

        if (!SaleActivity.submissionData.getValue(location)) {
            txtCreamSale?.hint = levels[ProductsEnum.Cream].toString()
            txtMilkSale?.hint = levels[ProductsEnum.Milk].toString()
            txtCupSale?.hint = levels[ProductsEnum.Cups].toString()
        } else {
            disableControls()
        }
    }

    override fun onClick(v: View) {
        fun Boolean.toInt() = if (this) 1 else 0

        val data: MutableMap<ProductsEnum, Int> = mutableMapOf()

        val textBoxes = listOf(
                txtCreamSale, txtMilkSale, txtCupSale
        )

        for (textBox in textBoxes) {
            if (textBox!!.text.toString() == "") {
                textBox.setText(textBox.hint)
            }
        }

        data[ProductsEnum.Cream] =
                levels.getValue(ProductsEnum.Cream) - txtCreamSale!!.text.toString().toInt()
        data[ProductsEnum.Milk] =
                levels.getValue(ProductsEnum.Milk) - txtMilkSale!!.text.toString().toInt()
        data[ProductsEnum.Soap] = swtchSoapSale!!.isChecked.toInt()
        data[ProductsEnum.Cups] =
                levels.getValue(ProductsEnum.Cups) - txtCupSale!!.text.toString().toInt()
        data[ProductsEnum.StirSticks] = swtchStirStickSale!!.isChecked.toInt()
        data[ProductsEnum.Coffee] = swtchCoffeeSale!!.isChecked.toInt()
        data[ProductsEnum.HotChocolate] = swtchHotChocolateSale!!.isChecked.toInt()
        data[ProductsEnum.Equal] = swtchEqualSale!!.isChecked.toInt()
        data[ProductsEnum.Sugar] = swtchSugarSale!!.isChecked.toInt()
        data[ProductsEnum.GreenTea] = swtchGreenTeaSale!!.isChecked.toInt()
        data[ProductsEnum.OrangePekoe] = swtchOrangePekoeSale!!.isChecked.toInt()
        data[ProductsEnum.CoffeeFilters] = swtchFilterSale!!.isChecked.toInt()

        val inventoryCount = InventoryCount(location, data)
        SQLConnection.submitCountToDatabase(inventoryCount)

        val prefs = saleActivity.getSharedPreferences(location.prefsFileName, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        for ((k, v) in data)
        {
            editor.putInt(k.productName, v)
        }

        editor.apply()

        SaleActivity.submissionData[location] = true

        SQLConnection.removeFromInventory(inventoryCount.productCount.filterValues { v -> v > 0 })

        disableControls()

        val tabLayout: TabLayout = saleActivity.findViewById(R.id.tabs)
        val currentTabIndex: Int = tabLayout.selectedTabPosition

        if (currentTabIndex + 1 == tabLayout.tabCount) {
            tabLayout.getTabAt(0)!!.select()
        } else {
            tabLayout.getTabAt(tabLayout.selectedTabPosition + 1)!!.select()
        }
    }

    private fun disableControls() {
        txtCreamSale?.hint = ""
        txtMilkSale?.hint = ""
        txtCupSale?.hint = ""
        txtCreamSale?.isEnabled = false
        txtMilkSale?.isEnabled = false
        txtCupSale?.isEnabled = false

        val switches = listOf(
                swtchSoapSale,
                swtchStirStickSale,
                swtchCoffeeSale,
                swtchHotChocolateSale,
                swtchEqualSale,
                swtchSugarSale,
                swtchGreenTeaSale,
                swtchOrangePekoeSale,
                swtchFilterSale
        )

        for (switch in switches) {
            switch!!.isEnabled = false
        }

        btnCommit!!.isEnabled = false
        btnCommit!!.setBackgroundColor(Color.GREEN)
    }
}