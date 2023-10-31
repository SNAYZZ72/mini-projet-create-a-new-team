import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import coil.load
import com.example.android.marsphotos.R
import com.example.android.marsphotos.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding


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

        val returnButton = binding.root.findViewById<ImageButton>(R.id.returnButton)
        val frameLayout = view.findViewById<FrameLayout>(R.id.imageContainer)

        // Chargez l'image en grand dans l'ImageView
        binding.detailImageView.load(imageUrl) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }

        returnButton.visibility = View.VISIBLE

        returnButton.setOnClickListener {
            frameLayout.visibility = View.GONE
            returnButton.visibility = View.GONE
        }

        binding.detailImageView.setOnClickListener {
            frameLayout.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val ARG_IMAGE_URL = "image_url"

        // Méthode pour créer une nouvelle instance de DetailFragment avec l'URL de l'image
        fun newInstance(imageUrl: String): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URL, imageUrl)
            fragment.arguments = args
            return fragment
        }
    }
}
