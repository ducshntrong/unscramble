package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import android.util.Log

/**
 * Đoạn nơi trò chơi được chơi, chứa logic trò chơi.
 */
class GameFragment : Fragment() {
    //tạo thuộc tính tham chiếu (đối tượng) cho ViewModel bên trong đơn vị điều khiển giao diện người dùng.
    private val viewModel: GameViewModel by viewModels()//Thêm viewModel
    // Syntax for property delegation
    //var <property-name> : <property-type> by <delegate-class>()

    private var score = 0
    private var currentWordCount = 0
    private var currentScrambledWord = "test"


    // Thể hiện đối tượng liên kết với quyền truy cập vào các chế độ xem trong bố cục game_fragment.xml
    private lateinit var binding: GameFragmentBinding

    // Tạo một ViewModel khi đoạn đầu tiên được tạo.
    // Nếu mảnh được tạo lại, nó sẽ nhận cùng một đối tượng GameViewModel được tạo bởi
    // đoạn đầu tiên

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate  tệp XML bố cục và trả về một thể hiện đối tượng liên kết
        binding = GameFragmentBinding.inflate(inflater, container, false)
        //Lệnh gọi lại onCreateView() sẽ được kích hoạt khi mảnh được tạo lần đầu tiên và cũng được
        //kích hoạt mỗi khi được tạo lại cho mọi sự kiện (chẳng hạn như thay đổi cấu hình).
        Log.d("GameFragment", "GameFragment created/re-created!")
        return binding.root
    }

// Phương thức này sẽ được gọi khi hoạt động và mảnh tương ứng bị huỷ
    override fun onDetach() {
        super.onDetach()
        Log.d("GameFragment", "GameFragment destroyed!")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Thiết lập trình nghe nhấp chuột cho các nút submit và skip.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
        // Update the UI
        updateNextWordOnScreen()
        binding.score.text = getString(R.string.score, 0)
        binding.wordCount.text = getString(
                R.string.word_count, 0, MAX_NO_OF_WORDS)
    }

    /*
     * Kiểm tra từ của người dùng và cập nhật điểm cho phù hợp.
     * Hiển thị từ xáo trộn tiếp theo.
     */
    private fun onSubmitWord() {
        currentScrambledWord = getNextScrambledWord()
        currentWordCount++
        score += SCORE_INCREASE
        binding.wordCount.text = getString(R.string.word_count, currentWordCount, MAX_NO_OF_WORDS)
        binding.score.text = getString(R.string.score, score)
        setErrorTextField(false)
    }

    /*
      * Bỏ qua từ hiện tại mà không thay đổi điểm số.
      * Tăng số lượng từ.
      */
    private fun onSkipWord() {
        currentScrambledWord = getNextScrambledWord()
        currentWordCount++
        binding.wordCount.text = getString(R.string.word_count, currentWordCount, MAX_NO_OF_WORDS)
        setErrorTextField(false)
    }

    /*
      * Lấy một từ ngẫu nhiên cho danh sách các từ và xáo trộn các chữ cái trong đó.
      */
    private fun getNextScrambledWord(): String {
        val tempWord = allWordsList.random().toCharArray()
        tempWord.shuffle()
        return String(tempWord)
    }

    /*
      * Khởi tạo lại dữ liệu trong ViewModel và cập nhật chế độ xem với dữ liệu mới để
      * khởi động lại trò chơi.
      */
    private fun restartGame() {
        setErrorTextField(false)
        updateNextWordOnScreen()
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
     * Đặt và đặt lại trạng thái lỗi trường văn bản.
     */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

    /*
      * Hiển thị từ xáo trộn tiếp theo trên màn hình.
      * phương thức updateNextWordOnScreen() để sử dụng thuộc tính viewModel chỉ có thể đọc là currentScrambledWord
      * Di chuyển dữ liệu sang ViewModel
      */
    private fun updateNextWordOnScreen() {
        binding.textViewUnscrambledWord.text = viewModel.currentScrambledWord
    }
}
