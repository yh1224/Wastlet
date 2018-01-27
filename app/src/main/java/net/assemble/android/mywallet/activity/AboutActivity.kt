package net.assemble.android.mywallet.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.salomonbrys.kodein.instance
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.assemble.android.common.activity.BaseActivity
import net.assemble.android.common.extensions.plusAssign
import net.assemble.android.common.util.RxBus
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.helper.PackageInfoHelper
import org.yaml.snakeyaml.Yaml
import java.io.FileNotFoundException

class AboutActivity : BaseActivity() {
    // Instances injected by Kodein
    private val packageInfoHelper: PackageInfoHelper by instance()
    private val bus: RxBus by instance()

    /** Disposable container for RxJava */
    private val disposables = CompositeDisposable()

    class LicenseAdapter(
            private val data: List<Map<String, Any>>,
            private val bus: RxBus
    ) : RecyclerView.Adapter<LicenseAdapter.ItemViewHolder>() {
        class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name = itemView.findViewById<TextView>(R.id.name)!!
            val copyrightHolder = itemView.findViewById<TextView>(R.id.copyrightHolder)!!
            val url = itemView.findViewById<TextView>(R.id.url)!!
            val license = itemView.findViewById<TextView>(R.id.license)!!
        }

        /** クリックイベント */
        class OnItemClickEvent(val url: String)

        override fun getItemCount() = data.count()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.license_info, parent, false)
            val holder = ItemViewHolder(itemView)
            itemView.setOnClickListener {
                val item = data[holder.adapterPosition]
                val url = item["url"] as String?
                if (url != null) {
                    bus.post(LicenseAdapter.OnItemClickEvent(url))
                }
            }
            return holder
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = data[position]
            holder.name.text = item["name"] as String? ?: ""
            holder.copyrightHolder.text = item["copyrightHolder"] as String? ?: ""
            holder.url.text = item["url"] as String? ?: ""
            holder.license.text = item["license"] as String? ?: ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_about)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)!!
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)

        // バージョン
        val version = findViewById<TextView>(R.id.version)!!
        version.text = getString(R.string.version, packageInfoHelper.getVersion())

        // ライセンス
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@AboutActivity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            itemAnimator = DefaultItemAnimator()

            // Read licenses from Yaml
            disposables += Single.fromCallable {
                try {
                    Yaml().load<List<Map<String, Any>>>(assets.open(LICENSES_YAML))
                            .filter { (it["skip"] as Boolean?) != true }
                } catch (e: FileNotFoundException) {
                    listOf<Map<String, Any>>()
                }
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { licenses ->
                        adapter = LicenseAdapter(licenses, bus)
                    }
        }

        // Support
        //val feedbackButton = findViewById<Button>(R.id.support)
        //feedbackButton.setOnClickListener {
        //    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.support_url)))
        //    startActivity(intent)
        //}

        bus.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { event ->
                    when (event) {
                        is LicenseAdapter.OnItemClickEvent -> {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                            startActivity(intent)
                        }
                    }
                }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    override fun onDestroy() {
        super.onDestroy()

        disposables.dispose()
    }

    companion object {
        @Suppress("unused")
        private val TAG = AboutActivity::class.java.simpleName

        private const val LICENSES_YAML = "licenses.yml"
    }
}
