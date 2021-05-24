package local.bryansapps.suprememanager

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class PlantDistributionFragment(private val location: LocationsEnum) : Fragment() {
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
        val view = inflater.inflate(R.layout.plant_distribution_fragment, container, false)

        val lblSubmissionDate = view.findViewById<TextView>(R.id.lblSubmissionDate)
        val lblSoapSale = view.findViewById<TextView>(R.id.txtSoapValue)
        val lblConeCupCaseSale = view.findViewById<TextView>(R.id.txtConeCupCaseValue)

        lblSubmissionDate.text = getString(R.string.FragmentDistribution_lblSubmissionDate_Text, prefs!!.getString("DateStamp", ""))

        lblSoapSale.text = prefs!!.getInt(ProductsEnum.Soap.productName, 0).toString()
        lblConeCupCaseSale.text = prefs!!.getInt(ProductsEnum.ConeCupCase.productName, 0).toString()

        return view
    }

    private fun passCountIntoSharedPrefs(mostRecentSubmissionDate: LocalDate) {
        val prefsEditor = prefs!!.edit()
        val mostRecentSubmissionData = SQLConnection.getMostRecentCountByLocation(location.locationCode, mostRecentSubmissionDate)

        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedString = mostRecentSubmissionDate.format(dtf)

        prefsEditor.putString("DateStamp", formattedString)

        for ((k, v) in mostRecentSubmissionData) {
            prefsEditor.putInt(k.productName, v)
        }

        prefsEditor.apply()
    }
}