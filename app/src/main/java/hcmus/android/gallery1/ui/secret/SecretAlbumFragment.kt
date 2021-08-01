package hcmus.android.gallery1.ui.secret

/*
class SecretAlbumFragment: ImageListFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_secret, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findElements()
        initBottomDrawer()
        bindButtons()
    }

    ////////////////////////////////////////////////////////////////////////////////

    private lateinit var bDrawerBehavior: BottomSheetBehavior<BottomNavigationView>
    private lateinit var bDrawerBtnExpand: ImageButton
    private lateinit var bDrawerDim: View
    private lateinit var btnClose: ImageButton

    ////////////////////////////////////////////////////////////////////////////////

    private fun findElements() {
        val view = requireView()

        bDrawerBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bdrawer_secure))
        bDrawerBtnExpand = view.findViewById(R.id.btn_bottom_sheet_expand)
        bDrawerDim = view.findViewById(R.id.bdrawer_secret_dim)
        btnClose = view.findViewById(R.id.btn_close_secret)
    }

    // Bottom drawer: behavior
    private fun initBottomDrawer() {

        // Bottom sheet behavior
        bDrawerBehavior.apply {
            isFitToContents = true
            // halfExpandedRatio = (490/1000f) // magic
        }

        // https://blog.mindorks.com/android-bottomsheet-in-kotlin
        bDrawerBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bDrawerDim.visibility = View.GONE
                        bDrawerBtnExpand.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_bdrawer_up))
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bDrawerDim.visibility = View.VISIBLE
                        bDrawerBtnExpand.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_bdrawer_down))
                    }
                    else -> { }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bDrawerDim.visibility = View.VISIBLE
                bDrawerDim.alpha = 0.5f * slideOffset
            }
        })

        bDrawerDim.setOnClickListener {
            bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // Button expansion behavior
        bDrawerBtnExpand.apply {
            setOnClickListener {
                when (bDrawerBehavior.state) {
                    BottomSheetBehavior.STATE_COLLAPSED     -> bDrawerBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    BottomSheetBehavior.STATE_EXPANDED      -> bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    else -> { }
                }
            }
        }
    }

    private fun bindButtons() {
        btnClose.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }
}
*/