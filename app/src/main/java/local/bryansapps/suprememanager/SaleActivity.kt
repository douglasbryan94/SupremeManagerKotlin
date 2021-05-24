package local.bryansapps.suprememanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager

class SaleActivity : AppCompatActivity() {
    private var levelsData: Map<LocationsEnum, Map<ProductsEnum, Int>>? = null
    companion object {
        val submissionData: MutableMap<LocationsEnum, Boolean> = SQLConnection.getCountSubmissionsForToday()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale)

        levelsData = SQLConnection.getAllLevelValues()

        val mViewPager: ViewPager = findViewById(R.id.view_pager)
        val mTabLayout: TabLayout = findViewById(R.id.tabs)

        setupViewPager(mViewPager)

        mTabLayout.setupWithViewPager(mViewPager)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = SectionsPageAdapter(supportFragmentManager)

        adapter.addFragment(SaleFragment(this, LocationsEnum.CorpDown, levelsData!!.getValue(LocationsEnum.CorpDown)), "Downstairs")
        adapter.addFragment(SaleFragment(this, LocationsEnum.CorpUp, levelsData!!.getValue(LocationsEnum.CorpUp)), "Upstairs")
        adapter.addFragment(SaleFragment(this, LocationsEnum.CorpKpo, levelsData!!.getValue(LocationsEnum.CorpKpo)), "KPO")
        adapter.addFragment(SaleFragment(this, LocationsEnum.CorpShip, levelsData!!.getValue(LocationsEnum.CorpShip)), "Shipping")
        adapter.addFragment(PlantSaleFragment(this, LocationsEnum.CorpPlant), "Plant")
        adapter.addFragment(SaleFragment(this, LocationsEnum.Solutions, levelsData!!.getValue(LocationsEnum.Solutions)), "Solutions")

        viewPager.adapter = adapter
    }
}