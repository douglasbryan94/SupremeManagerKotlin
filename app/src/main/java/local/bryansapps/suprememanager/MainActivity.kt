package local.bryansapps.suprememanager

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), DialogInterface.OnDismissListener {
    private var lblCreamValue: TextView? = null
    private var lblMilkValue: TextView? = null
    private var lblSoapValue: TextView? = null
    private var lblPaperCupValue: TextView? = null
    private var lblStirStickValue: TextView? = null
    private var lblCoffeeValue: TextView? = null
    private var lblHotChocolateValue: TextView? = null
    private var lblEqualValue: TextView? = null
    private var lblSugarValue: TextView? = null
    private var lblGreenTeaValue: TextView? = null
    private var lblOrangePekoeValue: TextView? = null

    private var btnDistribution: Button? = null
    private var btnPurchase: Button? = null
    private var btnCurrent: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance()
        } catch (ex: ClassNotFoundException) {
        } catch (ex: IllegalAccessException) {
        } catch (ex: InstantiationException) {
        }

        lblCreamValue = findViewById(R.id.lblCreamValue)
        lblMilkValue = findViewById(R.id.lblMilkValue)
        lblSoapValue = findViewById(R.id.lblSoapValue)
        lblPaperCupValue = findViewById(R.id.lblPaperCupValue)
        lblStirStickValue = findViewById(R.id.lblStirStickValue)
        lblCoffeeValue = findViewById(R.id.lblCoffeeValue)
        lblHotChocolateValue = findViewById(R.id.lblHotChocolateValue)
        lblEqualValue = findViewById(R.id.lblEqualValue)
        lblSugarValue = findViewById(R.id.lblSugarValue)
        lblGreenTeaValue = findViewById(R.id.lblGreenTeaValue)
        lblOrangePekoeValue = findViewById(R.id.lblOrangePekoeValue)

        btnDistribution = findViewById(R.id.btnDistribution)
        btnPurchase = findViewById(R.id.btnPurchase)
        btnCurrent = findViewById(R.id.btnCurrent)

        btnDistribution!!.setOnClickListener {
            startActivity(Intent(this, DistributionActivity::class.java))
        }

        btnPurchase!!.setOnClickListener {
            PurchaseDialog().show(supportFragmentManager, "PurchaseProductFragment")
        }

        btnCurrent!!.setOnClickListener {
            startActivity(Intent(this, SaleActivity::class.java))
        }

        refreshShoppingList()
    }

    private fun refreshShoppingList() {
        val inv: Map<ProductsEnum, Int> = SQLConnection.getAllInventoryValues()

        lblCreamValue!!.text = (inv.getValue(ProductsEnum.Cream) * -1).toString()
        lblMilkValue!!.text = (inv.getValue(ProductsEnum.Milk) * -1).toString()
        lblSoapValue!!.text = (inv.getValue(ProductsEnum.Soap) * -1).toString()
        lblPaperCupValue!!.text = (inv.getValue(ProductsEnum.Cups) * -1).toString()
        lblStirStickValue!!.text = (inv.getValue(ProductsEnum.StirSticks) * -1).toString()
        lblCoffeeValue!!.text = (inv.getValue(ProductsEnum.Coffee) * -1).toString()
        lblHotChocolateValue!!.text = (inv.getValue(ProductsEnum.HotChocolate) * -1).toString()
        lblEqualValue!!.text = (inv.getValue(ProductsEnum.Equal) * -1).toString()
        lblSugarValue!!.text = (inv.getValue(ProductsEnum.Sugar) * -1).toString()
        lblGreenTeaValue!!.text = (inv.getValue(ProductsEnum.GreenTea) * -1).toString()
        lblOrangePekoeValue!!.text = (inv.getValue(ProductsEnum.OrangePekoe) * -1).toString()
    }

    override fun onRestart() {
        super.onRestart()

        refreshShoppingList()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        refreshShoppingList()
    }
}