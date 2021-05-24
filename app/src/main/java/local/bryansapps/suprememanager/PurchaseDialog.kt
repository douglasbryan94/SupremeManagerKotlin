package local.bryansapps.suprememanager

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment


class PurchaseDialog : DialogFragment() {
    private var product: Spinner? = null
    private var quantity: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity).setCancelable(false)
        val inflater = requireActivity().layoutInflater

        val view = inflater.inflate(R.layout.purchase_product_dialog_layout, null)
        product = view.findViewById(R.id.spnProduct)
        quantity = view.findViewById(R.id.txtQuantity)
        val acceptButton = view.findViewById<Button>(R.id.btnAcceptPurchase)
        val cancelButton = view.findViewById<Button>(R.id.btnCancelPurchase)

        acceptButton.setOnClickListener {
            val product = ProductsEnum.valueOf(product?.selectedItem.toString().replace("\\s".toRegex(), ""))
            val value = quantity?.text.toString().toIntOrNull()

            if (value != null) {
                SQLConnection.addToInventory(
                        product,
                        value
                )

                this.dismiss()
            }
        }

        cancelButton.setOnClickListener {
            this.dismiss()
        }

        builder.setView(view)

        return builder.create()
    }

    override fun onDestroyView() {
        val dialog = dialog

        if ((dialog != null) && retainInstance) {
            dialog.setDismissMessage(null)
        }

        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val activity: Activity? = activity
        if (activity is DialogInterface.OnDismissListener) {
            (activity as DialogInterface.OnDismissListener).onDismiss(dialog)
        }
    }
}