package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }

    /**
     * ViewModelは、関連付けられたフラグメントの接続が解除されたとき、またはアクティビティが終了したときに破棄されます。
     * https://developer.android.com/codelabs/basic-android-kotlin-training-viewmodel?hl=ja&continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-kotlin-unit-3-pathway-3%3Fhl%3Dja%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-training-viewmodel#5
     * 画面が回転しただけだと廃棄されない。
     */
    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }

    /*
     * Updates currentWord and currentScrambledWord with the next word.
     */
    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        // シャッフルしても元の単語と同じなら繰り返す。
        // codelabではここのコードに誤りがあった。
        // shuffleのような現実にはあまり出くわさない処理の周りのバグは
        // やっぱり気づきにくいんだろうな。
        // ただ、whileの中の処理は一回は通すようにテストするというのは
        // 当たり前だから、実際の業務では大丈夫なのかもしれない。
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        // ゲーム中に出てきた単語だったら選び直す
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }
    }

    /*
     * Returns true if the current word count is less than MAX_NO_OF_WORDS.
     * Updates the next word.
     */
    fun nextWord(): Boolean {
        return if (currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    /*
     * Re-initializes the game data to restart the game.
     */
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }
}