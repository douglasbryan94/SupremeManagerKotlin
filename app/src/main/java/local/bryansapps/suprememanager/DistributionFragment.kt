package local.bryansapps.suprememanager

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class DistributionFragment(private val location: LocationsEnum) : Fragment() {
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = activity!!.getSharedPreferences(location.prefsFileName, Context.MODE_PRIVATE)
        var mostRecentSubmissionDate: LocalDate? = null

        try {
            val latestDate = LocalDate.parse(prefs!!.getString("DateStamp", ""))

            if (latestDate != LocalDate.now()) {
                mostRecentSubmissionDate = LocalDate.parse(SQLConnection.getMostRecentCountSubmissionForLocation(location.locationCode))

                if (latestDate != mostRecentSubmissionDate) {
                    passCountIntoSharedPrefs(mostRecentSubmissionDate!!)
                }
            }
        } catch (ex: DateTimeParseException) {
            mostRecentSubmissionDate = LocalDate.parse(SQLConnection.getMostRecentCountSubmissionForLocation(location.locationCode))
            passCountIntoSharedPrefs(mostRecentSubmissionDate!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.distribution_fragment, container, false)

        val lblSubmissionDate = view.findViewById<TextView>(R.id.lblSubmissionDate)
        val lblCreamSale = view.findViewById<TextView>(R.id.txtCreamValue)
        val lblMilkSale = view.findViewById<TextView>(R.id.txtMilkValue)
        val lblSoapSale = view.findViewById<TextView>(R.id.txtSoapValue)
        val lblCupSale = view.findViewById<TextView>(R.id.txtPaperCupValue)
        val lblStirStickSale = view.findViewById<TextView>(R.id.txtStirStickValue)
        val lblCoffeeSale = view.findViewById<TextView>(R.id.txtCoffeeValue)
        val lblHotChocolateSale = view.findViewById<TextView>(R.id.txtHotChocolateValue)
        val lblEqualSale = view.findViewById<TextView>(R.id.txtEqualValue)
        val lblSugarSale = view.findViewById<TextView>(R.id.txtSugarValue)
        val lblGreenTeaSale = view.findViewById<TextView>(R.id.txtGreenTeaValue)
        val lblOrangePekoeSale = view.findViewById<TextView>(R.id.txtOrangePekoeValue)
        val chkFiltersSale = view.findViewById<CheckBox>(R.id.chkFilters)

        lblSubmissionDate.text = getString(R.string.FragmentDistribution_lblSubmissionDate_Text, prefs!!.getString("DateStamp", ""))

        lblCreamSale.text = prefs!!.getInt(ProductsEnum.Cream.productName, 0).toString()
        lblMilkSale.text = prefs!!.getInt(ProductsEnum.Milk.productName, 0).toString()
        lblSoapSale.text = prefs!!.getInt(ProductsEnum.Soap.productName, 0).toString()
        lblCupSale.text = prefs!!.getInt(ProductsEnum.Cups.productName, 0).toString()
        lblStirStickSale.text = prefs!!.getInt(ProductsEnum.StirSticks.productName, 0).toString()
        lblCoffeeSale.text = prefs!!.getInt(ProductsEnum.Coffee.productName, 0).toString()
        lblHotChocolateSale.text = prefs!!.getInt(ProductsEnum.HotChocolate.productName, 0).toString()
        lblEqualSale.text = prefs!!.getInt(ProductsEnum.Equal.productName, 0).toString()
        lblSugarSale.text = prefs!!.getInt(ProductsEnum.Sugar.productName, 0).toString()
        lblGreenTeaSale.text = prefs!!.getInt(ProductsEnum.GreenTea.productName, 0).toString()
        lblOrangePekoeSale.text = prefs!!.getInt(ProductsEnum.OrangePekoe.productName, 0).toString()
        chkFiltersSale.isChecked = prefs!!.getBoolean(ProductsEnum.CoffeeFilters.productName, false)

        return view
    }

    private fun passCountIntoSharedPrefs(mostRecentSubmissionDate: LocalDate) {
        fun Int.toBool() = this == 1

        val prefsEditor = prefs!!.edit()
        val mostRecentSubmissionData = SQLConnection.getMostRecentCountByLocation(location.locationCode, mostRecentSubmissionDate)

        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedString = mostRecentSubmissionDate.format(dtf)

        prefsEditor.putString("DateStamp", formattedString)

        for ((k, v) in mostRecentSubmissionData) {

            if (k == ProductsEnum.CoffeeFilters) {
                prefsEditor.putBoolean(k.productName, v.toBool())
            } else {
                prefsEditor.putInt(k.productName, v)
            }
        }

        prefsEditor.apply()
    }
}