import org.junit.Test

class GetNextWordTest {
    @Test
    fun how_to_compare_wrong_strings() {
        val currentWord = "icon"
        val tempWord = currentWord.toCharArray()
        // toStringしてもオブジェクトのハッシュコードが表示されるだけ
        println(tempWord.toString())
        println(tempWord.toString().equals(currentWord, false))
    }

    /**
     * 正しいstringへの戻し方
     * see https://github.com/google-developer-training/android-basics-kotlin-unscramble-app/commit/867daa1b0280525bdd19fb955fba8f918ebc0f2c
     */
    @Test
    fun correct(){
        val currentWord = "icon"
        val tempWord = currentWord.toCharArray()
        println(String(tempWord).equals(currentWord, false))
    }
}