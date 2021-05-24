package local.bryansapps.suprememanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager


class DistributionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distribution)

        val mViewPager: ViewPager = findViewById(R.id.view_pager)
        val mTabLayout: TabLayout = findViewById(R.id.tabs)

        mViewPager.offscreenPageLimit = 5

        setupViewPager(mViewPager)

        mTabLayout.setupWithViewPager(mViewPager)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = SectionsPageAdapter(supportFragmentManager)

        adapter.addFragment(DistributionFragment(LocationsEnum.CorpDown), "Downstairs")
        adapter.addFragment(DistributionFragment(LocationsEnum.CorpUp), "Upstairs")
        adapter.addFragment(DistributionFragment(LocationsEnum.CorpKpo), "KPO")
        adapter.addFragment(DistributionFragment(LocationsEnum.CorpShip), "Shipping")
        adapter.addFragment(PlantDistributionFragment(LocationsEnum.CorpPlant), "Plant")
        adapter.addFragment(DistributionFragment(LocationsEnum.Solutions), "Solutions")

        viewPager.adapter = adapter
    }
}