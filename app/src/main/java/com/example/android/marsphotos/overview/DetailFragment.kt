import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.MarsDatabase
import com.example.android.marsphotos.data.MarsPhoto
import com.example.android.marsphotos.databinding.FragmentDetailBinding
import com.example.android.marsphotos.overview.OverviewViewModel
import com.example.android.marsphotos.overview.OverviewViewModelFactory
import com.example.android.marsphotos.repository.MarsRepository
import kotlin.properties.Delegates

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private var isLiked: Boolean = false
    private var photoId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Récupérez l'URL de l'image passée en argument
        val imageUrl = arguments?.getString(ARG_IMAGE_URL)
        val favoriteButton = binding.root.findViewById<ImageButton>(R.id.favoriteButton)

        val returnButton = binding.root.findViewById<ImageButton>(R.id.returnButton)
        val frameLayout = view.findViewById<FrameLayout>(R.id.imageContainer)

        // Chargez l'image en grand dans l'ImageView
        binding.detailImageView.load(imageUrl) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }

        returnButton.visibility = View.VISIBLE
        favoriteButton.visibility = View.VISIBLE

        photoId = arguments?.getString(ARG_PHOTO_ID) ?: ""

        val viewModel = ViewModelProvider(requireActivity(), OverviewViewModelFactory(MarsRepository(MarsDatabase.getInstance(requireContext()).marsPhotoDao, requireContext()), MarsDatabase.getInstance(requireContext()))).get(OverviewViewModel::class.java)
        val marsPhoto = viewModel.getPhotoById(photoId)

        isLiked = marsPhoto?.liked ?: false
        updateFavoriteButtonState()

        returnButton.setOnClickListener {
            frameLayout.visibility = View.GONE
            favoriteButton.visibility = View.GONE
            returnButton.visibility = View.GONE
        }


        favoriteButton.setOnClickListener {
            isLiked = !isLiked
            marsPhoto?.liked = isLiked
            viewModel.updatePhoto(marsPhoto)
            updateFavoriteButtonState()
        }

        binding.detailImageView.setOnClickListener {
            frameLayout.visibility = View.VISIBLE
        }
    }

    private fun updateFavoriteButtonState() {
        // Mettez à jour l'apparence du bouton "J'aime" en fonction de l'état "liked"
        val favoriteButton = binding.root.findViewById<ImageButton>(R.id.favoriteButton)
        favoriteButton.setImageResource(if (isLiked) R.drawable.ic_favorite_24 else R.drawable.ic_favorite_border_24)
    }

    companion object {
        private const val ARG_IMAGE_URL = "image_url"
        private const val ARG_IS_LIKED = "is_liked"
        private const val ARG_PHOTO_ID = "photo_id"
        private const val ARG_REPOSITORY = "repository"
        private const val ARG_DATABASE = "database"

        fun newInstance(imageUrl: String, isLiked: Boolean, photoId: String, repository: MarsRepository, database: MarsDatabase): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URL, imageUrl)
            args.putBoolean(ARG_IS_LIKED, isLiked)
            args.putString(ARG_PHOTO_ID, photoId)
            args.putSerializable(ARG_REPOSITORY, repository)
            args.putSerializable(ARG_DATABASE, database)
            fragment.arguments = args
            return fragment
        }
    }
}

private fun Bundle.putSerializable(argRepository: String, database: MarsDatabase) {

}

private fun Bundle.putSerializable(argRepository: String, repository: MarsRepository) {

}
