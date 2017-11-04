import com.accountcreator.Credentials
import com.accountcreator.randomCredentials
import org.junit.Test

class CredentialsTest {
    @Test
    fun testCredentials() {
        val list = mutableListOf<Credentials>()
        for (i in 1..1000) {
            list.add(randomCredentials(2, 11, 15))
        }

        assert(list.none { it.password.length > 15 })
        assert(list.none { it.password.length < 11 })
        assert(list.none { it.password.contains("""/""") })
    }
}

