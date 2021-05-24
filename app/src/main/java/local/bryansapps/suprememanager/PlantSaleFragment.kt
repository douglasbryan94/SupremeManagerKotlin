package local.bryansapps.suprememanager

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PlantSaleFragment(
    private val saleActivity: SaleActivity,
    private val location: LocationsEnum
) : Fragment(), View.OnClickListener {
    private var swtchSoapSale: SwitchCompat? = null
    private var swtchConeCupCaseSale: SwitchCompat? = null
    private var btnCommit: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.plant_sale_fragment, container, false)

        swtchSoapSale = view.findViewById(R.id.swtchSoap)
        swtchConeCupCaseSale = view.findViewById(R.id.swtchConeCupsCase)

        btnCommit = view.findViewById(R.id.btnCommit)
        btnCommit!!.setOnClickListener(this)

        return view
    }

    override fun onStart() {
        super.onStart()

        if (SaleActivity.submissionData.getValue(location)) {
            disableControls()
        }
    }

    override fun onClick(v: View) {
        fun Boolean.toInt() = if (this) 1 else 0

        val data: MutableMap<ProductsEnum, Int> = mutableMapOf()

        data[ProductsEnum.Soap] = swtchSoapSale!!.isChecked.toInt()
        data[ProductsEnum.ConeCupCase] = swtchConeCupCaseSale!!.isChecked.toInt()

        val inventoryCount = InventoryCount(location, data)
        SQLConnection.submitCountToDatabase(inventoryCount)

        val prefs = activity!!.getSharedPreferences(location.prefsFileName, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedString = LocalDate.now().format(dtf)

        editor.putString("DateStamp", formattedString)

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
        swtchSoapSale!!.isEnabled = false
        swtchConeCupCaseSale!!.isEnabled = false

        btnCommit!!.isEnabled = false
        btnCommit!!.setBackgroundColor(Color.GREEN)
    }
}